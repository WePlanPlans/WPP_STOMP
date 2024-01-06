package org.tenten.tentenstomp.global.messaging.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.global.response.GlobalStompResponse;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, GlobalStompResponse<?> message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
