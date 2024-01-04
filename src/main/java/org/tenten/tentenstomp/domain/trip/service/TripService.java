package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripRequestMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripResponseMsg;
import org.tenten.tentenstomp.domain.trip.pubsub.RedisPublisher;
import org.tenten.tentenstomp.domain.trip.pubsub.RedisSubscriber;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripItemRepository tripItemRepository;
    private final RedisPublisher redisPublisher;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final Map<String, ChannelTopic> channelTopicMap = new HashMap<>();
    private final Map<String, HashSet<Long>> connectedMemberMap = new HashMap<>();

//    @Transactional
//    public Long save(TripCreateRequest request) {
//
//        return null;
//    }

    @Transactional
    public void updateTrip(String tripId, TripRequestMsg request) {
        // 여정 업데이트 로직 작성
        ChannelTopic topic = getChannelTopic(tripId, request.endPoint());
        TripResponseMsg tripResponseMsg = new TripResponseMsg(request.tripId(), request.visitDate(), request.endPoint(), null, null, null);
        redisPublisher.publish(topic, tripResponseMsg); // 해당 여정의 토픽을 찾아야함,
    }

    private ChannelTopic getChannelTopic(String tripId, String endPoint) {
        String channelName = tripId + endPoint;
        if (!channelTopicMap.containsKey(channelName)) {
            ChannelTopic newTopic = new ChannelTopic(channelName);
            channelTopicMap.put(channelName, newTopic);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, newTopic);
        }
        return channelTopicMap.get(channelName);
    }
    @Transactional
    public void updateMember(String tripId, TripRequestMsg request) {
        ChannelTopic topic = getChannelTopic(tripId, request.endPoint());
        TripResponseMsg tripResponseMsg = new TripResponseMsg(request.tripId(), request.visitDate(), request.endPoint(), null, null, null);
        redisPublisher.publish(topic, tripResponseMsg); // 해당 여정의 토픽을 찾아야함,
    }

    @Transactional
    public void updatePlace(String tripId, TripRequestMsg request) {
        ChannelTopic topic = getChannelTopic(tripId, request.endPoint());
        TripResponseMsg tripResponseMsg = new TripResponseMsg(request.tripId(), request.visitDate(), request.endPoint(), null, null, null);
        redisPublisher.publish(topic, tripResponseMsg); // 해당 여정의 토픽을 찾
    }
}
