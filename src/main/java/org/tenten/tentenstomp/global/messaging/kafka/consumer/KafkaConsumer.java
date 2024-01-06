package org.tenten.tentenstomp.global.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.domain.trip.dto.request.TripUpdateMsg;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SimpMessageSendingOperations messagingTemplate;

    @KafkaListener(topics = "kafka", groupId = ConsumerConfig.GROUP_ID_CONFIG)
    public void consumeTest(TripUpdateMsg data) {
        messagingTemplate.convertAndSend("/sub/kafka", data);
    }
}
