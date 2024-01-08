package org.tenten.tentenstomp.global.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.domain.trip.dto.request.TripUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.global.response.GlobalStompResponse;
import org.tenten.tentenstomp.global.util.TopicUtil;

import java.time.LocalDate;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TopicUtil topicUtil;

    @KafkaListener(topics = "kafka", groupId = GROUP_ID_CONFIG)
    public void consumeTest(TripUpdateMsg data) {
        messagingTemplate.convertAndSend("/sub/kafka", data);
    }

    @KafkaListener(topics = TRIP_INFO, groupId = GROUP_ID_CONFIG)
    public void updateTripInfo(TripInfoMsg tripInfoMsg) {
        messagingTemplate.convertAndSend(topicUtil.topicToReturnEndPoint(tripInfoMsg.tripId(), TRIP_INFO), GlobalStompResponse.ok(tripInfoMsg));
    }

    @KafkaListener(topics = TRIP_ITEM, groupId = GROUP_ID_CONFIG)
    public void updateTripItem(TripItemMsg tripItemMsg) {
        messagingTemplate.convertAndSend(topicUtil.topicToReturnEndPoint(tripItemMsg.tripId(), TRIP_ITEM, LocalDate.parse(tripItemMsg.visitDate())), GlobalStompResponse.ok(tripItemMsg));
    }

    @KafkaListener(topics = PATH, groupId = GROUP_ID_CONFIG)
    public void updateTripPath(TripPathMsg tripPathMsg) {
        messagingTemplate.convertAndSend(topicUtil.topicToReturnEndPoint(tripPathMsg.tripId(), PATH, LocalDate.parse(tripPathMsg.visitDate())), GlobalStompResponse.ok(tripPathMsg));
    }

    @KafkaListener(topics = MEMBER, groupId = GROUP_ID_CONFIG)
    public void updateConnectedTripMember(TripMemberMsg tripMemberMsg) {
        messagingTemplate.convertAndSend(topicUtil.topicToReturnEndPoint(tripMemberMsg.tripId(), MEMBER), GlobalStompResponse.ok(tripMemberMsg));
    }

    @KafkaListener(topics = BUDGET, groupId = GROUP_ID_CONFIG)
    public void updateBudget(TripBudgetMsg tripBudgetMsg) {
        messagingTemplate.convertAndSend(topicUtil.topicToReturnEndPoint(tripBudgetMsg.tripId(), BUDGET), GlobalStompResponse.ok(tripBudgetMsg));

    }
}
