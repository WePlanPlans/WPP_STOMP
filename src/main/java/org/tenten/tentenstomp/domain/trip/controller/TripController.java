package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.domain.trip.dto.request.*;
import org.tenten.tentenstomp.domain.trip.service.TripService;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;
    private final KafkaProducer kafkaProducer;
    /*
    TODO : 백엔드에서 예외가 발생하면, 프론트로 예외 발생하기 전 시점 데이터를 보내줘야하는데, 이걸 어떻게 할 수 있을까
    TODO : 실시간 편집인데, 노션 처럼 누가 어떤 것을 변경했는지 알려줄 필요가 있지 않을까?
     */


    @MessageMapping("/trips/{tripId}/connectMember")
    public void connectMember(@DestinationVariable String tripId, @Payload MemberConnectMsg memberConnectMsg) {
        tripService.connectMember(tripId, memberConnectMsg);
    }

    @MessageMapping("/trips/{tripId}/getConnectedMember")
    public void getConnectedMember(@DestinationVariable String tripId) {
        tripService.getConnectedMember(tripId);
    }

    @MessageMapping("/trips/{tripId}/disconnectMember")
    public void disconnectMember(@DestinationVariable String tripId, @Payload MemberDisconnectMsg memberDisconnectMsg) {
        tripService.disconnectMember(tripId, memberDisconnectMsg);
    }

    @MessageMapping("/trips/{tripId}/enterMember")
    public void enterMember(@DestinationVariable String tripId, @Payload MemberConnectMsg memberConnectMsg) {
        tripService.enterMember(tripId, memberConnectMsg);
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

    @MessageMapping("/trips/{tripId}/getPathAndItems")
    public void getPathAndItems(@DestinationVariable String tripId, @Payload PathAndItemRequestMsg pathAndItemRequestMsg) {
        tripService.getPathAndItems(tripId, pathAndItemRequestMsg);
    }

    @MessageMapping("/trips/{tripId}/updateBudget")
    public void updateTripBudget(@DestinationVariable String tripId, @Payload TripBudgetUpdateMsg tripBudgetUpdateMsg) {
        tripService.updateTripBudget(tripId, tripBudgetUpdateMsg);
    }

    @MessageMapping("/trips/{tripId}/updateTransportation")
    public void updateTransportation(@DestinationVariable String tripId, @Payload TripTransportationUpdateMsg tripTransportationUpdateMsg) {
        tripService.updateTripTransportation(tripId, tripTransportationUpdateMsg);
    }
}
