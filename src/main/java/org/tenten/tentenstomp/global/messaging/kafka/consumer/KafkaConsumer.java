package org.tenten.tentenstomp.global.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.global.response.GlobalStompResponse;
import org.tenten.tentenstomp.global.util.TopicUtil;

import java.time.LocalDate;

import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TopicUtil topicUtil;

    @KafkaListener(topics = TRIP_INFO, groupId = GROUP_ID_CONFIG)
    public void updateTripInfo(TripInfoMsg tripInfoMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripInfoMsg.tripId(), TRIP_INFO);
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripInfoMsg));
    }

    @KafkaListener(topics = TRIP_ITEM, groupId = GROUP_ID_CONFIG)
    public void updateTripItem(TripItemMsg tripItemMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripItemMsg.tripId(), TRIP_ITEM, LocalDate.parse(tripItemMsg.visitDate()));
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripItemMsg));
    }

    @KafkaListener(topics = PATH, groupId = GROUP_ID_CONFIG)
    public void updateTripPath(TripPathMsg tripPathMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripPathMsg.tripId(), PATH, LocalDate.parse(tripPathMsg.visitDate()));
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripPathMsg));
    }

    @KafkaListener(topics = MEMBER, groupId = GROUP_ID_CONFIG)
    public void updateConnectedTripMember(TripMemberMsg tripMemberMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripMemberMsg.tripId(), MEMBER);
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripMemberMsg));
    }

    @KafkaListener(topics = BUDGET, groupId = GROUP_ID_CONFIG)
    public void updateBudget(TripBudgetMsg tripBudgetMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripBudgetMsg.tripId(), BUDGET);
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripBudgetMsg));
    }

    @KafkaListener(topics = CURSOR, groupId = GROUP_ID_CONFIG)
    public void updateCursor(TripCursorMsg tripCursorMsg) {
        String destination = topicUtil.topicToReturnEndPoint(tripCursorMsg.tripId(), CURSOR, LocalDate.parse(tripCursorMsg.visitDate()));
        log.info(destination);
        messagingTemplate.convertAndSend(destination, GlobalStompResponse.ok(tripCursorMsg));
    }
}
