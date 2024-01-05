package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.*;
import org.tenten.tentenstomp.domain.trip.dto.response.TripInfoMsg;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.global.common.constant.EndPointConstant;
import org.tenten.tentenstomp.global.publisher.RedisPublisher;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;
import org.tenten.tentenstomp.global.response.GlobalStompResponse;
import org.tenten.tentenstomp.global.util.RedisChannelUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.tenten.tentenstomp.global.common.constant.EndPointConstant.*;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final RedisChannelUtil redisChannelUtil;
    private final TripItemRepository tripItemRepository;
    private final RedisPublisher redisPublisher;

    private final Map<String, HashSet<Long>> connectedMemberMap = new HashMap<>();
//    private final PathComponent pathComponent;

    @Transactional
    public void updateTrip(String tripId, TripUpdateMsg tripUpdateMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
        ChannelTopic topic = redisChannelUtil.getChannelTopic(tripId, TRIP_INFO);

        TripInfoMsg tripResponseMsg = trip.changeTripInfo(tripUpdateMsg);
        tripRepository.save(trip);
        redisPublisher.publish(topic, GlobalStompResponse.ok(tripResponseMsg)); // 해당 여정의 토픽을 찾아야함,
    }
    @Transactional
    public void addTripItem(String tripId, TripItemAddMsg tripItemAddMsg) {
        Trip trip = tripRepository.getReferenceById(Long.parseLong(tripId));
        ChannelTopic tripItemTopic = redisChannelUtil.getChannelTopic(tripId, tripItemAddMsg.newTripItems().get(0).visitDate(), TRIP_ITEM);
        ChannelTopic pathTopic = redisChannelUtil.getChannelTopic(tripId, tripItemAddMsg.newTripItems().get(0).visitDate(), PATH);

        // TODO : /sub/{tripId}/tripItems/{visitDate}
        // TODO : /sub/{tripId}/path/{visitDate}

    }
    @Transactional
    public void updateTripItemOrder(String tripId, TripItemOrderUpdateMsg orderUpdateMsg) {
        // TODO : /sub/{tripId}/tripItems/{visitDate}
        // TODO : /sub/{tripId}/path/{visitDate}
        ChannelTopic tripItemTopic = redisChannelUtil.getChannelTopic(tripId, tripItemAddMsg.newTripItems().get(0).visitDate(), TRIP_ITEM);
        ChannelTopic pathTopic = redisChannelUtil.getChannelTopic(tripId, tripItemAddMsg.newTripItems().get(0).visitDate(), PATH);


    }
    @Transactional(readOnly = true)
    public void connectMember(String tripId, MemberConnectMsg memberConnectMsg) {
        // TODO: /sub/{tripId}/connectedMember
        ChannelTopic memberTopic = redisChannelUtil.getChannelTopic(tripId, MEMBER);
    }
    @Transactional(readOnly = true)
    public void disconnectMember(String tripId, MemberDisconnectMsg memberDisconnectMsg) {
        // TODO: /sub/{tripId}/connectedMember
        ChannelTopic memberTopic = redisChannelUtil.getChannelTopic(tripId, MEMBER);
    }



}
