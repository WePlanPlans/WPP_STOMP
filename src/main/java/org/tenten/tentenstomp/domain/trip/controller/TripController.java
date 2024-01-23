package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.tenten.tentenstomp.domain.trip.dto.request.*;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemAddResponse;
import org.tenten.tentenstomp.domain.trip.service.TripService;

import static org.tenten.tentenstomp.global.common.constant.ResponseConstant.DELETED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;

    @PostMapping("/trips/{tripId}")
    public ResponseEntity<TripItemAddResponse> addTripItemFromMainPage(
        @PathVariable(name = "tripId") String tripId,
        @RequestBody TripItemAddRequest tripItemAddRequest) {
        return ResponseEntity.ok(tripService.addTripItemFromMainPage(tripId, tripItemAddRequest));
    }

    @DeleteMapping("/trips/{tripId}/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable(name = "tripId") String tripId, @PathVariable(name = "memberId") Long memberId) {
        tripService.deleteTripMember(tripId, memberId);
        return ResponseEntity.ok(DELETED);
    }

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
    public void enterMember(@DestinationVariable String tripId) {
        tripService.enterMember(tripId);
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

    @MessageMapping("/trips/{tripId}/cursor")
    public void updateUserCursor(@DestinationVariable String tripId, @Payload CursorUpdateMsg cursorUpdateMsg) {
        tripService.updateCursor(tripId, cursorUpdateMsg);
    }
}
