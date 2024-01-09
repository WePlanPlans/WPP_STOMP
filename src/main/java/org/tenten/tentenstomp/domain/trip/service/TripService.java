package org.tenten.tentenstomp.domain.trip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;
import org.tenten.tentenstomp.global.cache.RedisCache;
import org.tenten.tentenstomp.global.common.enums.Category;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

import java.time.LocalDate;
import java.util.*;

import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripItemRepository tripItemRepository;
    private final MemberRepository memberRepository;
    private final TourItemRepository tourItemRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisCache redisCache;
    private final PathComponent pathComponent;
    private final ObjectMapper objectMapper;
    private final Map<String, HashMap<Long, TripMemberInfoMsg>> tripConnectedMemberMap = new HashMap<>();

    @Transactional
    public void connectMember(String tripId, MemberConnectMsg memberConnectMsg) {
        HashMap<Long, TripMemberInfoMsg> connectedMemberMap = tripConnectedMemberMap.getOrDefault(tripId, new HashMap<>());
        Optional<TripMemberInfoMsg> tripMemberInfoByMemberId = memberRepository.findTripMemberInfoByMemberId(memberConnectMsg.memberId());
        tripMemberInfoByMemberId.ifPresent(tripMemberInfoMsg -> connectedMemberMap.put(memberConnectMsg.memberId(), tripMemberInfoMsg));

        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            Long.parseLong(tripId), connectedMemberMap.values().stream().toList(), memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId))
        );
        tripConnectedMemberMap.put(tripId, connectedMemberMap);
        sendToKafkaAndSave(tripMemberMsg);

    }

    @Transactional
    public void disconnectMember(String tripId, MemberDisconnectMsg memberDisconnectMsg) {
        HashMap<Long, TripMemberInfoMsg> connectedMemberMap = tripConnectedMemberMap.getOrDefault(tripId, new HashMap<>());
        connectedMemberMap.remove(memberDisconnectMsg.memberId());

        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            Long.parseLong(tripId), connectedMemberMap.values().stream().toList(), memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId))
        );
        tripConnectedMemberMap.put(tripId, connectedMemberMap);
        sendToKafkaAndSave(tripMemberMsg);

    }

    @Transactional
    public void enterMember(String tripId, MemberConnectMsg memberConnectMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        kafkaProducer.send(MEMBER, getTripMemberMsg(tripId));
        kafkaProducer.send(TRIP_INFO, trip.toTripInfo());
        kafkaProducer.send(TRIP_ITEM, getTripItemMsg(trip, trip.getStartDate().toString()));
        kafkaProducer.send(PATH, getTripPathMsg(trip, trip.getStartDate().toString()));
        kafkaProducer.send(BUDGET, getTripBudgetMsg(trip));
    }


    @Transactional
    public void updateTrip(String tripId, TripUpdateMsg tripUpdateMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        TripInfoMsg tripInfoMsg = trip.changeTripInfo(tripUpdateMsg);
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(
            trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum()
        );
        tripRepository.save(trip);

        sendToKafkaAndSave(tripInfoMsg, tripBudgetMsg);

    }

    @Transactional
    public void addTripItem(String tripId, TripItemAddMsg tripItemAddMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
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

    private void updateBudgetAndItemsAndPath(Trip trip, List<TripItem> tripItems, String visitDate) {
        TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(tripItems));
        Map<String, Long> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate, 0L), tripPath.pathPriceSum());
        tripPathPriceMap.put(visitDate, tripPath.pathPriceSum());
        tripRepository.save(trip);

        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
        TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), visitDate, tripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate, tripPath.tripPathInfoMsgs());

        sendToKafkaAndSave(tripBudgetMsg, tripItemMsg, tripPathMsg);
    }

    @Transactional
    public void updateTripItemOrder(String tripId, TripItemOrderUpdateMsg orderUpdateMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
        Map<Long, Long> itemOrderMap = new HashMap<>();
        for (OrderInfo orderInfo : orderUpdateMsg.tripItemOrder()) {
            itemOrderMap.put(orderInfo.tripItemId(), orderInfo.seqNum());
        }
        List<TripItem> tripItems = trip.getTripItems();
        for (TripItem tripItem : tripItems) {
            tripItem.updateSeqNum(itemOrderMap.get(tripItem.getId()));
        }
        updateBudgetAndItemsAndPath(trip, tripItems, orderUpdateMsg.visitDate());

    }

    @Transactional(readOnly = true)
    public void getPathAndItems(String tripId, PathAndItemRequestMsg pathAndItemRequestMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));

        kafkaProducer.send(TRIP_ITEM, getTripItemMsg(trip, pathAndItemRequestMsg.visitDate()));
        kafkaProducer.send(PATH, getTripPathMsg(trip, pathAndItemRequestMsg.visitDate()));
    }

    private void sendToKafkaAndSave(Object... dataArgs) {
        for (Object data : dataArgs) {
            if (data.getClass().equals(TripPathMsg.class)) {
                kafkaProducer.send(PATH, data);
                TripPathMsg tripPathMsg = (TripPathMsg) data;
                redisCache.save(PATH, Long.toString(tripPathMsg.tripId()), tripPathMsg.visitDate(), tripPathMsg);
            }
            if (data.getClass().equals(TripItemMsg.class)) {
                kafkaProducer.send(TRIP_ITEM, data);
                TripItemMsg tripItemMsg = (TripItemMsg) data;
                redisCache.save(TRIP_ITEM, Long.toString(tripItemMsg.tripId()), tripItemMsg.visitDate(), tripItemMsg);
            }
            if (data.getClass().equals(TripInfoMsg.class)) {
                kafkaProducer.send(TRIP_INFO, data);
                TripInfoMsg tripInfoMsg = (TripInfoMsg) data;
                redisCache.save(TRIP_INFO, Long.toString(tripInfoMsg.tripId()), tripInfoMsg);
            }
            if (data.getClass().equals(TripMemberMsg.class)) {
                kafkaProducer.send(MEMBER, data);
                TripMemberMsg tripMemberMsg = (TripMemberMsg) data;
                redisCache.save(MEMBER, Long.toString(tripMemberMsg.tripId()), tripMemberMsg);
            }
            if (data.getClass().equals(TripBudgetMsg.class)) {
                kafkaProducer.send(BUDGET, data);
                TripBudgetMsg tripBudgetMsg = (TripBudgetMsg) data;
                redisCache.save(BUDGET, Long.toString(tripBudgetMsg.tripId()), tripBudgetMsg);
            }
        }
    }

    private TripMemberMsg getTripMemberMsg(String tripId) {
        Object cached = redisCache.get(MEMBER, tripId);
        if (cached != null) {
            return (TripMemberMsg) cached;
        }
        HashMap<Long, TripMemberInfoMsg> connectedMemberMap = tripConnectedMemberMap.getOrDefault(tripId, new HashMap<>());
        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            Long.parseLong(tripId), connectedMemberMap.values().stream().toList(), memberRepository.findTripMemberInfoByTripId(Long.parseLong(tripId))
        );
        redisCache.save(MEMBER, tripId, tripMemberMsg);
        return tripMemberMsg;
    }

    private TripBudgetMsg getTripBudgetMsg(Trip trip) {

        Object cached = redisCache.get(BUDGET, Long.toString(trip.getId()));
        if (cached != null) {
            return objectMapper.convertValue(cached, TripBudgetMsg.class);
        }
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(
            trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum()
        );
        redisCache.save(BUDGET, Long.toString(trip.getId()), tripBudgetMsg);
        return tripBudgetMsg;
    }

    private TripItemMsg getTripItemMsg(Trip trip, String visitDate) {
        Object cached = redisCache.get(TRIP_ITEM, Long.toString(trip.getId()), visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripItemMsg.class);

        }
        List<TripItemInfo> tripInfos = tripItemRepository.getTripItemInfoByTripIdAndVisitDate(trip.getId(), LocalDate.parse(visitDate));
        List<TripItemInfoMsg> tripItemInfoMsgs = tripInfos.stream().map(t -> new TripItemInfoMsg(
            t.tripItemId(), t.tourItemId(), t.name(), t.thumbnailUrl(), Category.fromCode(t.contentTypeId()).getName(), t.transportation(), t.seqNum(), t.visitDate().toString(), t.price()
        )).toList();
        TripItemMsg tripItemMsg = new TripItemMsg(trip.getId(), visitDate, tripItemInfoMsgs);
        redisCache.save(TRIP_ITEM, Long.toString(trip.getId()), visitDate, tripItemMsg);
        return tripItemMsg;

    }

    private TripPathMsg getTripPathMsg(Trip trip, String visitDate) {
        Object cached = redisCache.get(PATH, Long.toString(trip.getId()), visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripPathMsg.class);
        }
        TripPathCalculationResult tripPath = pathComponent.getTripPath(tripItemRepository.findTripPlaceByTripIdAndVisitDate(trip.getId(), LocalDate.parse(visitDate)));
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate, tripPath.tripPathInfoMsgs());
        redisCache.save(PATH, Long.toString(trip.getId()), visitDate, tripPathMsg);
        return tripPathMsg;

    }


}
