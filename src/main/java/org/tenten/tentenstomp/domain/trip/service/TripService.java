package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.tenten.tentenstomp.global.common.enums.TripStatus;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;
import org.tenten.tentenstomp.global.util.SecurityUtil;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg.fromTripItemList;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;
import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;
import static org.tenten.tentenstomp.global.common.enums.Transportation.fromName;
import static org.tenten.tentenstomp.global.common.enums.TripStatus.*;
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
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
        connectedMember.add(securityUtil.getMemberId(memberConnectMsg.token()));

        List<TripMemberInfoMsg> tripMembers = memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId)).stream().map(
            tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
        ).toList();
        TripMemberMsg tripMemberMsg = sortTripMemberMsg(tripId, tripMembers, trip);
        tripConnectedMemberMap.put(tripId, connectedMember);
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);
    }

    private static TripMemberMsg sortTripMemberMsg(String tripId, List<TripMemberInfoMsg> tripMembers, Trip trip) {
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
        return new TripMemberMsg(
            Long.parseLong(tripId),
            tripMemberInfoMsgs,
            trip.getNumberOfPeople()
        );
    }

    @Transactional
    public void getConnectedMember(String tripId) {
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            Long.parseLong(tripId),
            memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId)).stream().map(
                tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
            ).toList(),
            trip.getNumberOfPeople()
        );
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);
    }

    @Transactional
    public void disconnectMember(String tripId, MemberDisconnectMsg memberDisconnectMsg) {
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
        connectedMember.remove(securityUtil.getMemberId(memberDisconnectMsg.token()));

        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            Long.parseLong(tripId),
            memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId)).stream().map(
                tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
            ).toList(),
            trip.getNumberOfPeople()
        );
        tripConnectedMemberMap.put(tripId, connectedMember);
        kafkaProducer.sendAndSaveToRedis(tripMemberMsg);

    }

    @Transactional
    public void enterMember(String tripId) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        kafkaProducer.send(MEMBER, messageProxyRepository.getTripMemberMsg(trip.getId(), tripConnectedMemberMap));
        kafkaProducer.send(TRIP_INFO, trip.toTripInfo());
        kafkaProducer.send(TRIP_ITEM, messageProxyRepository.getTripItemMsg(trip.getId(), trip.getStartDate().toString()));
        kafkaProducer.send(PATH, messageProxyRepository.getTripPathMsg(trip.getId(), trip.getStartDate().toString()));
        kafkaProducer.send(BUDGET, messageProxyRepository.getTripBudgetMsg(trip));
    }


    @Transactional
    public void updateTrip(String tripId, TripUpdateMsg tripUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(Long.parseLong(tripId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없다.", NOT_FOUND));

        TripInfoMsg tripInfoMsg = trip.changeTripInfo(tripUpdateMsg);
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(
            trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum()
        );
        tripRepository.save(trip);

        kafkaProducer.sendAndSaveToRedis(tripInfoMsg, tripBudgetMsg);

    }

    @Transactional
    public void addTripItem(String tripId, TripItemAddMsg tripItemAddMsg) {
        Trip trip = tripRepository.findTripForUpdate(Long.parseLong(tripId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(Long.parseLong(tripId), LocalDate.parse(tripItemAddMsg.visitDate()));
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
        TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(tripItems), fromName(transportation));
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate, 0), tripPath.pathPriceSum());
        tripPathPriceMap.put(visitDate, tripPath.pathPriceSum());
        trip.updateTripPathPriceMap(tripPathPriceMap);
        tripRepository.save(trip);

        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
        TripItemMsg tripItemMsg = fromTripItemList(trip.getId(), visitDate, fromName(transportation), tripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate, fromName(transportation), tripPath.tripPathInfoMsgs());

        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg, tripPathMsg);
    }


    @Transactional
    public void updateTripItemOrder(String tripId, TripItemOrderUpdateMsg orderUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(Long.parseLong(tripId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        Map<Long, Long> itemOrderMap = new HashMap<>();
        for (OrderInfo orderInfo : orderUpdateMsg.tripItemOrder()) {
            itemOrderMap.put(orderInfo.tripItemId(), orderInfo.seqNum());
        }
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(trip.getId(), LocalDate.parse(orderUpdateMsg.visitDate()));
        for (TripItem tripItem : tripItems) {
            tripItem.updateSeqNum(itemOrderMap.get(tripItem.getId()));
        }
        updateBudgetAndItemsAndPath(trip, tripItems, orderUpdateMsg.visitDate());

    }

    @Transactional
    public void getPathAndItems(String tripId, PathAndItemRequestMsg pathAndItemRequestMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        kafkaProducer.send(TRIP_ITEM, messageProxyRepository.getTripItemMsg(trip.getId(), pathAndItemRequestMsg.visitDate()));
        kafkaProducer.send(PATH, messageProxyRepository.getTripPathMsg(trip.getId(), pathAndItemRequestMsg.visitDate()));
    }

    @Transactional
    public void updateTripBudget(String tripId, TripBudgetUpdateMsg tripBudgetUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(Long.parseLong(tripId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));

        trip.updateBudget(tripBudgetUpdateMsg.budget());
        LocalDate now = LocalDate.now();
        TripStatus tripStatus;
        if (now.isBefore(trip.getStartDate())) {
            tripStatus = BEFORE;
        } else if (now.isAfter(trip.getEndDate())) {
            tripStatus = AFTER;
        } else {
            tripStatus = ING;
        }
        TripInfoMsg tripInfoMsg = new TripInfoMsg(trip.getId(), trip.getStartDate().toString(), trip.getEndDate().toString(), trip.getNumberOfPeople(), trip.getTripName(), tripStatus, trip.getBudget());
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripInfoMsg);
    }

    @Transactional
    public void updateTripTransportation(String tripId, TripTransportationUpdateMsg tripTransportationUpdateMsg) {
        Trip trip = tripRepository.findTripForUpdate(Long.parseLong(tripId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
        String visitDate = tripTransportationUpdateMsg.visitDate();
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(trip.getId(), LocalDate.parse(visitDate));

        TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(tripItems), tripTransportationUpdateMsg.transportation());
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate, 0), tripPath.pathPriceSum());

        tripTransportationMap.put(visitDate, tripTransportationUpdateMsg.transportation().getName());
        tripPathPriceMap.put(visitDate, tripPath.pathPriceSum());

        trip.updateTripPathPriceMap(tripPathPriceMap);
        trip.updateTripTransportationMap(tripTransportationMap);
        tripRepository.save(trip);

        updateSeqNum(tripItems);
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
        TripItemMsg tripItemMsg = fromTripItemList(trip.getId(), visitDate, tripTransportationUpdateMsg.transportation(), tripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate, tripTransportationUpdateMsg.transportation(), tripPath.tripPathInfoMsgs());

        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg, tripPathMsg);
    }
    @Transactional
    public TripItemAddResponse addTripItemFromMainPage(Long tripId, TripItemAddRequest tripItemAddRequest) {
        Trip trip = tripRepository.findTripForUpdate(tripId).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정이 없습니다 " + tripId, NOT_FOUND));
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripId, LocalDate.parse(tripItemAddRequest.visitDate()));
        LocalDate visitDate = LocalDate.parse(tripItemAddRequest.visitDate());
        TripItem entity = TripItemCreateRequest.toEntity(tourItemRepository.getReferenceById(Long.parseLong(tripItemAddRequest.tourItemId())), trip, (long) tripItems.size() + 1, visitDate);
        tripItemRepository.save(entity);
        tripItems.add(entity);

        updateBudgetAndItemsAndPath(trip, tripItems, tripItemAddRequest.visitDate());

        return new TripItemAddResponse(trip.getId(), entity.getId(), Long.parseLong(tripItemAddRequest.tourItemId()), tripItemAddRequest.visitDate());
    }
}
