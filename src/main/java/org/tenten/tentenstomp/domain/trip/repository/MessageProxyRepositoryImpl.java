package org.tenten.tentenstomp.domain.trip.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.member.repository.MemberRepository;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.global.cache.RedisCache;
import org.tenten.tentenstomp.global.common.enums.Category;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Repository
@RequiredArgsConstructor
public class MessageProxyRepositoryImpl implements MessageProxyRepository{
    private final RedisCache redisCache;
    private final ObjectMapper objectMapper;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripItemRepository tripItemRepository;
    private final PathComponent pathComponent;
    @Transactional(readOnly = true)
    public TripMemberMsg getTripMemberMsg(Long tripId, Map<String, HashMap<Long, TripMemberInfoMsg>> tripConnectedMemberMap) {
        Object cached = redisCache.get(MEMBER, Long.toString(tripId));
        if (cached != null) {
            return objectMapper.convertValue(cached, TripMemberMsg.class);
        }
        HashMap<Long, TripMemberInfoMsg> connectedMemberMap = tripConnectedMemberMap.getOrDefault(Long.toString(tripId), new HashMap<>());
        Trip trip = tripRepository.getReferenceById(tripId);
        TripMemberMsg tripMemberMsg = new TripMemberMsg(
            tripId, connectedMemberMap.values().stream().toList(), memberRepository.findTripMemberInfoByTripId(tripId), trip.getNumberOfPeople()
        );
        redisCache.save(MEMBER, Long.toString(tripId), tripMemberMsg);
        return tripMemberMsg;
    }
    @Transactional(readOnly = true)
    public TripBudgetMsg getTripBudgetMsg(Trip trip) {

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
    @Transactional(readOnly = true)
    public TripItemMsg getTripItemMsg(Long tripId, String visitDate) {
        Object cached = redisCache.get(TRIP_ITEM, Long.toString(tripId), visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripItemMsg.class);

        }
        List<TripItemInfo> tripInfos = tripItemRepository.getTripItemInfoByTripIdAndVisitDate(tripId, LocalDate.parse(visitDate));
        List<TripItemInfoMsg> tripItemInfoMsgs = tripInfos.stream().map(t -> new TripItemInfoMsg(
            t.tripItemId(), t.tourItemId(), t.name(), t.thumbnailUrl(), Category.fromCode(t.contentTypeId()).getName(), t.transportation(), t.seqNum(), t.visitDate().toString(), t.price()
        )).toList();
        TripItemMsg tripItemMsg = new TripItemMsg(tripId, visitDate, tripItemInfoMsgs);
        redisCache.save(TRIP_ITEM, Long.toString(tripId), visitDate, tripItemMsg);
        return tripItemMsg;

    }
    @Transactional(readOnly = true)
    public TripPathMsg getTripPathMsg(Long tripId, String visitDate) {
        Object cached = redisCache.get(PATH, Long.toString(tripId), visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripPathMsg.class);
        }
        TripPathCalculationResult tripPath = pathComponent.getTripPath(tripItemRepository.findTripPlaceByTripIdAndVisitDate(tripId, LocalDate.parse(visitDate)));
        TripPathMsg tripPathMsg = new TripPathMsg(tripId, visitDate, tripPath.tripPathInfoMsgs());
        redisCache.save(PATH, Long.toString(tripId), visitDate, tripPathMsg);
        return tripPathMsg;

    }
}
