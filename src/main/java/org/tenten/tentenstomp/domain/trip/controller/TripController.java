package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.domain.trip.dto.request.*;
import org.tenten.tentenstomp.domain.trip.service.TripService;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final KafkaProducer kafkaProducer;

    @MessageMapping("/kafka")
    public void testKafka(@Payload TripUpdateMsg tripUpdateMsg) {
        kafkaProducer.send("kafka", tripUpdateMsg);
    }

    @MessageMapping("/trips/{tripId}/info")
    public void editPlan(@DestinationVariable String tripId, @Payload TripUpdateMsg tripUpdateMsg) {
        tripService.updateTrip(tripId, tripUpdateMsg);
    }

    @MessageMapping("/trips/{tripId}/addTripItems")
    public void addTripItem(@DestinationVariable String tripId, @Payload TripItemAddMsg tripItemAddMsg) {
        tripService.addTripItem(tripId, tripItemAddMsg);
    }

    @MessageMapping("/trips/{tripId}/updateTripItemOrder")
    public void updateTripItemOrder(@DestinationVariable String tripId, @Payload TripItemOrderUpdateMsg orderUpdateMsg) {
        tripService.updateTripItemOrder(tripId, orderUpdateMsg);
    }

    @MessageMapping("/trips/{tripId}/connectMember")
    public void connectMember(@DestinationVariable String tripId, @Payload MemberConnectMsg memberConnectMsg) {
        tripService.connectMember(tripId, memberConnectMsg);
    }

    @MessageMapping("/trips/{tripId}/disconnectMember")
    public void disconnectMember(@DestinationVariable String tripId, @Payload MemberDisconnectMsg memberDisconnectMsg) {
        tripService.disconnectMember(tripId, memberDisconnectMsg);
    }

    @MessageMapping("/trips/{tripId}/enterMember")
    public void enterMember(@DestinationVariable String tripId, @Payload MemberConnectMsg memberConnectMsg) {
        tripService.enterMember(tripId, memberConnectMsg);
    }

    @MessageMapping("/trips/{tripId}/getPathAndItems")
    public void getPathAndItems(@DestinationVariable String tripId, @Payload PathAndItemRequestMsg pathAndItemRequestMsg) {
        tripService.getPathAndItems(tripId, pathAndItemRequestMsg);
    }
}
