package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.domain.trip.dto.request.TripRequestMsg;
import org.tenten.tentenstomp.domain.trip.pubsub.RedisPublisher;
import org.tenten.tentenstomp.domain.trip.service.TripService;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final RedisPublisher redisPublisher;

//    @PostMapping("/trip")
//    public Long createPlan(@RequestBody TripCreateRequest request) {
//        // 여행 계획을 데이터베이스에 저장하고, 고유한 ID를 생성합니다.
//        return tripService.save(request);
//    }



    @MessageMapping("/trips/{tripId}/info")
    public void editPlan(@DestinationVariable String tripId, @Payload TripRequestMsg requestMsg) {
        // 여행 계획을 데이터베이스에 업데이트하고, 업데이트된 계획을 반환합니다.
        tripService.updateTrip(tripId, requestMsg);
    }

    @MessageMapping("/trips/{tripId}/member")
    public void editMember(@DestinationVariable String tripId, @Payload TripRequestMsg requestMsg) {
        tripService.updateMember(tripId, requestMsg);
    }

    @MessageMapping("/trips/{tripId}/place")
    public void editPlace(@DestinationVariable String tripId, @Payload TripRequestMsg requestMsg) {
        tripService.updatePlace(tripId, requestMsg);
    }
}
