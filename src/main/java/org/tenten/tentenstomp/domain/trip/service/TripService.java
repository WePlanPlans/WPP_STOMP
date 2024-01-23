package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.member.entity.Member;
import org.tenten.tentenstomp.domain.member.repository.MemberRepository;
import org.tenten.tentenstomp.domain.tour.repository.TourItemRepository;
import org.tenten.tentenstomp.domain.trip.dto.request.*;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemAddMsg.TripItemCreateRequest;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemOrderUpdateMsg.OrderInfo;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.repository.MessageProxyRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;
import org.tenten.tentenstomp.global.util.SecurityUtil;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripInfoMsg.fromEntity;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg.fromTripItemList;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripMemberMsg.fromEntity;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.PATH;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.TRIP_ITEM;
import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;
import static org.tenten.tentenstomp.global.common.enums.Transportation.fromName;
import static org.tenten.tentenstomp.global.component.dto.request.TripPlace.fromTripItems;
import static org.tenten.tentenstomp.global.util.SequenceUtil.updateSeqNum;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripItemRepository tripItemRepository;
    private final MemberRepository memberRepository;
    private final TourItemRepository tourItemRepository;
    private final KafkaProducer kafkaProducer;
    private final PathComponent pathComponent;
    private final MessageProxyRepository messageProxyRepository;
    private final SecurityUtil securityUtil;
    private final Map<String, HashSet<Long>> tripConnectedMemberMap = new HashMap<>();
    @Transactional
    public void connectMember(String tripId, MemberConnectMsg memberConnectMsg) {
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));
        connectedMember.add(securityUtil.getMemberId(memberConnectMsg.token()));

        List<TripMemberInfoMsg> tripMembers = memberRepository.findTripMemberInfoByTripId(tripId).stream().map(
            tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
        ).toList();
        TripMemberMsg tripMemberMsg = sortTripMemberMsg(tripMembers, trip);
        tripConnectedMemberMap.put(tripId, connectedMember);
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);
    }

    private static TripMemberMsg sortTripMemberMsg(List<TripMemberInfoMsg> tripMembers, Trip trip) {
        List<TripMemberInfoMsg> tripMemberInfoMsgs = new ArrayList<>();
        for (TripMemberInfoMsg tripMemberMsg : tripMembers) {
            if (tripMemberMsg.connected()) {
                tripMemberInfoMsgs.add(tripMemberMsg);
            }
        }
        for (TripMemberInfoMsg tripMemberMsg : tripMembers) {
            if (!tripMemberMsg.connected()) {
                tripMemberInfoMsgs.add(tripMemberMsg);
            }
        }
        return fromEntity(trip, tripMemberInfoMsgs);
    }

    @Transactional
    public void getConnectedMember(String tripId) {
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));
        kafkaProducer.sendAndSaveToRedis(messageProxyRepository.getTripMemberMsg(trip.getEncryptedId(), tripConnectedMemberMap));
    }

    @Transactional
    public void disconnectMember(String tripId, MemberDisconnectMsg memberDisconnectMsg) {
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));
        connectedMember.remove(securityUtil.getMemberId(memberDisconnectMsg.token()));

        List<TripMemberInfoMsg> tripMemberInfoMsgs = memberRepository.findTripMemberInfoByTripId(tripId).stream().map(
            tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
        ).toList();

        TripMemberMsg tripMemberMsg = fromEntity(trip, tripMemberInfoMsgs);
        tripConnectedMemberMap.put(tripId, connectedMember);
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);

    }

    @Transactional
    public void enterMember(String tripId) {
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));

        kafkaProducer.sendWithOutCaching(
            messageProxyRepository.getTripMemberMsg(trip.getEncryptedId(), tripConnectedMemberMap),
            trip.toTripInfo(),
            messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), trip.getStartDate().toString()),
            messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), trip.getStartDate().toString()),
            messageProxyRepository.getTripBudgetMsg(trip)
        );
    }


    @Transactional
    public void updateTrip(String tripId, TripUpdateMsg tripUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다.", NOT_FOUND));

        TripInfoMsg tripInfoMsg = trip.changeTripInfo(tripUpdateMsg);

        LocalDate startDate = trip.getStartDate();
        LocalDate endDate = trip.getEndDate();
        LocalDate currentDate = startDate;
        Integer transportationPriceSum = 0;
        Long itemPriceSum = 0L;
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        while (!currentDate.isAfter(endDate)) {
            transportationPriceSum += tripPathPriceMap.getOrDefault(currentDate.toString(), 0);
            itemPriceSum += tripItemRepository.findTripItemPriceSumByTripIdAndVisitDate(tripId, currentDate);
            currentDate = currentDate.plusDays(1L);
        }

        trip.updateTripItemPriceSum(itemPriceSum);
        trip.updateTransportationPriceSum(transportationPriceSum);

        TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);
        tripRepository.save(trip);

        kafkaProducer.sendAndSaveToRedis(tripInfoMsg, tripBudgetMsg);

    }

    @Transactional
    public void addTripItem(String tripId, TripItemAddMsg tripItemAddMsg) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripId, LocalDate.parse(tripItemAddMsg.visitDate()));
        LocalDate visitDate = LocalDate.parse(tripItemAddMsg.visitDate());
        List<TripItem> newTripItems = new ArrayList<>();
        for (TripItemCreateRequest tripItemCreateRequest : tripItemAddMsg.newTripItems()) {
            TripItem entity = TripItemCreateRequest.toEntity(tourItemRepository.getReferenceById(tripItemCreateRequest.tourItemId()), trip, (long) tripItems.size() + 1, visitDate);
            newTripItems.add(entity);
            tripItems.add(entity);
        }
        tripItemRepository.saveAll(newTripItems);

        updateBudgetAndItemsAndPath(trip, tripItems, tripItemAddMsg.visitDate());

    }

    @Transactional
    public void updateBudgetAndItemsAndPath(Trip trip, List<TripItem> tripItems, String visitDate) {
        Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
        String transportation = tripTransportationMap.getOrDefault(visitDate, CAR.getName());
        updateSeqNum(tripItems);
        TripPathCalculationResult tripPath = pathComponent.getTripPath(fromTripItems(tripItems), fromName(transportation));
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate, 0), tripPath.pathPriceSum());
        tripPathPriceMap.put(visitDate, tripPath.pathPriceSum());
        trip.updateTripPathPriceMap(tripPathPriceMap);
        tripRepository.save(trip);

        TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);
        TripItemMsg tripItemMsg = fromTripItemList(trip.getEncryptedId(), visitDate, fromName(transportation), tripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getEncryptedId(), visitDate, fromName(transportation), tripPath.tripPathInfoMsgs());

        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg, tripPathMsg);
    }


    @Transactional
    public void updateTripItemOrder(String tripId, TripItemOrderUpdateMsg orderUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        Map<Long, Long> itemOrderMap = new HashMap<>();
        for (OrderInfo orderInfo : orderUpdateMsg.tripItemOrder()) {
            itemOrderMap.put(orderInfo.tripItemId(), orderInfo.seqNum());
        }
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(trip.getEncryptedId(), LocalDate.parse(orderUpdateMsg.visitDate()));
        for (TripItem tripItem : tripItems) {
            tripItem.updateSeqNum(itemOrderMap.get(tripItem.getId()));
        }
        updateBudgetAndItemsAndPath(trip, tripItems, orderUpdateMsg.visitDate());

    }

    @Transactional
    public void getPathAndItems(String tripId, PathAndItemRequestMsg pathAndItemRequestMsg) {
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));

        kafkaProducer.send(TRIP_ITEM, messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), pathAndItemRequestMsg.visitDate()));
        kafkaProducer.send(PATH, messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), pathAndItemRequestMsg.visitDate()));
    }

    @Transactional
    public void updateTripBudget(String tripId, TripBudgetUpdateMsg tripBudgetUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));

        trip.updateBudget(tripBudgetUpdateMsg.budget());
        TripInfoMsg tripInfoMsg = fromEntity(trip);
        TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);
        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripInfoMsg);
    }

    @Transactional
    public void updateTripTransportation(String tripId, TripTransportationUpdateMsg tripTransportationUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
        String visitDate = tripTransportationUpdateMsg.visitDate();
        tripTransportationMap.put(visitDate, tripTransportationUpdateMsg.transportation().getName());
        trip.updateTripTransportationMap(tripTransportationMap);
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(trip.getEncryptedId(), LocalDate.parse(visitDate));
        updateBudgetAndItemsAndPath(trip, tripItems, visitDate);

    }

    @Transactional
    public TripItemAddResponse addTripItemFromMainPage(String tripId, TripItemAddRequest tripItemAddRequest) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripId, LocalDate.parse(tripItemAddRequest.visitDate()));
        LocalDate visitDate = LocalDate.parse(tripItemAddRequest.visitDate());
        TripItem entity = TripItemCreateRequest.toEntity(tourItemRepository.getReferenceById(Long.parseLong(tripItemAddRequest.tourItemId())), trip, (long) tripItems.size() + 1, visitDate);
        tripItemRepository.save(entity);
        tripItems.add(entity);

        updateBudgetAndItemsAndPath(trip, tripItems, tripItemAddRequest.visitDate());

        return new TripItemAddResponse(trip.getEncryptedId(), entity.getId(), Long.parseLong(tripItemAddRequest.tourItemId()), tripItemAddRequest.visitDate());
    }

    @Transactional
    public void deleteTripMember(String tripId, Long memberId) {
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        connectedMember.remove(memberId);
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다 " + tripId, NOT_FOUND));
        List<TripMemberInfoMsg> tripMemberInfoMsgs = memberRepository.findTripMemberInfoByTripId(tripId).stream().map(
            tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
        ).toList();
        TripMemberMsg tripMemberMsg = fromEntity(trip, tripMemberInfoMsgs);
        tripConnectedMemberMap.put(tripId, connectedMember);
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);

    }

    @Transactional
    public void updateCursor(String tripId, CursorUpdateMsg cursorUpdateMsg) {
        Long memberId = securityUtil.getMemberId(cursorUpdateMsg.token());
        Member member = memberRepository.getReferenceById(memberId);
        TripCursorMsg tripCursorMsg = new TripCursorMsg(tripId, cursorUpdateMsg.visitDate(), memberId, member.getNickname(), cursorUpdateMsg.x(), cursorUpdateMsg.y());
        kafkaProducer.sendAndSaveToRedis(tripCursorMsg);
    }
}
