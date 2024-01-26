package org.tenten.tentenstomp.domain.trip.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.member.repository.MemberRepository;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.global.cache.RedisCache;
import org.tenten.tentenstomp.global.common.enums.Category;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.exception.GlobalException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripMemberMsg.fromEntity;
import static org.tenten.tentenstomp.global.common.constant.ErrorMsgConstant.NOT_FOUND_TRIP;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;
import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;
import static org.tenten.tentenstomp.global.common.enums.Transportation.fromName;
import static org.tenten.tentenstomp.global.component.dto.request.TripPlace.fromTripItems;

@Repository
@RequiredArgsConstructor
public class MessageProxyRepositoryImpl implements MessageProxyRepository {
    private final RedisCache redisCache;
    private final ObjectMapper objectMapper;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripItemRepository tripItemRepository;
    private final PathComponent pathComponent;

    @Transactional(readOnly = true)
    public TripMemberMsg getTripMemberMsg(String tripId, Map<String, HashSet<Long>> tripConnectedMemberMap) {
        Object cached = redisCache.get(MEMBER, tripId);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripMemberMsg.class);
        }
        HashSet<Long> connectedMember = tripConnectedMemberMap.getOrDefault(tripId, new HashSet<>());
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException(NOT_FOUND_TRIP + tripId, NOT_FOUND));
        List<TripMemberInfoMsg> tripMembers = memberRepository.findTripMemberInfoByTripId(tripId).stream().map(
            tm -> new TripMemberInfoMsg(tm.memberId(), tm.name(), tm.thumbnailUrl(), connectedMember.contains(tm.memberId()))
        ).toList();
        TripMemberMsg tripMemberMsg = sortTripMemberMsg(tripMembers, trip);
        redisCache.save(MEMBER, tripId, tripMemberMsg);
        return tripMemberMsg;
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

    @Transactional(readOnly = true)
    public TripBudgetMsg getTripBudgetMsg(Trip trip) {

        Object cached = redisCache.get(BUDGET, trip.getEncryptedId());
        if (cached != null) {
            return objectMapper.convertValue(cached, TripBudgetMsg.class);
        }
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(
            trip.getEncryptedId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum()
        );
        redisCache.save(BUDGET, trip.getEncryptedId(), tripBudgetMsg);
        return tripBudgetMsg;
    }

    @Transactional(readOnly = true)
    public TripItemMsg getTripItemMsg(String tripId, String visitDate) {
        Object cached = redisCache.get(TRIP_ITEM, tripId, visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripItemMsg.class);

        }
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException(NOT_FOUND_TRIP + tripId, NOT_FOUND));
        Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
        String transportation = tripTransportationMap.getOrDefault(visitDate, CAR.getName());
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripId, LocalDate.parse(visitDate));
        List<TripItemInfoMsg> tripItemInfoMsgs = tripItems.stream().map(t -> new TripItemInfoMsg(
            t.getId(), t.getTourItem().getId(), t.getTourItem().getTitle(), t.getTourItem().getOriginalThumbnailUrl(), Category.fromCode(t.getTourItem().getContentTypeId()).getName(), t.getSeqNum(), t.getVisitDate().toString(), t.getPrice()
        )).toList();
        TripItemMsg tripItemMsg = new TripItemMsg(tripId, visitDate, fromName(transportation), tripItemInfoMsgs);
        redisCache.save(TRIP_ITEM, tripId, visitDate, tripItemMsg);
        return tripItemMsg;

    }

    @Transactional(readOnly = true)
    public TripPathMsg getTripPathMsg(String tripId, String visitDate) {
        Object cached = redisCache.get(PATH, tripId, visitDate);
        if (cached != null) {
            return objectMapper.convertValue(cached, TripPathMsg.class);
        }
        Trip trip = tripRepository.findByEncryptedId(tripId).orElseThrow(() -> new GlobalException(NOT_FOUND_TRIP + tripId, NOT_FOUND));
        Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
        String transportation = tripTransportationMap.getOrDefault(visitDate, CAR.getName());
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripId, LocalDate.parse(visitDate));
        TripPathCalculationResult tripPath = pathComponent.getTripPath(fromTripItems(tripItems), fromName(transportation));
        TripPathMsg tripPathMsg = new TripPathMsg(tripId, visitDate, fromName(transportation), tripPath.tripPathInfoMsgs());
        redisCache.save(PATH, tripId, visitDate, tripPathMsg);
        return tripPathMsg;

    }
}
