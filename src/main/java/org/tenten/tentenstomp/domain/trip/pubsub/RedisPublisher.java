package org.tenten.tentenstomp.domain.trip.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.domain.trip.dto.request.TripRequestMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripResponseMsg;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, TripResponseMsg message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
