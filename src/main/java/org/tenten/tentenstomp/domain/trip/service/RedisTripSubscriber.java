package org.tenten.tentenstomp.domain.trip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisTripSubscriber implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations sendingOperations;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 어떤 채널을 사용하고 있는지 알아야 하기 때문에 메시지의 채널을 가져옴
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);

            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            TripPublishDto tripPublishDto = objectMapper.readValue(publishMessage, TripPublishDto.class);

            // 메시지의 채널로 메시지를 전송
            sendingOperations.convertAndSend(channel, tripPublishDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
