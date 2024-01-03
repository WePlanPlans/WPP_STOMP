package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.domain.trip.dto.request.TripCreateRequest;
import org.tenten.tentenstomp.domain.trip.dto.request.TripEditRequest;
import org.tenten.tentenstomp.domain.trip.dto.response.TripEditResponse;
import org.tenten.tentenstomp.domain.trip.service.TripService;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping("/trip")
    public Long createPlan(@RequestBody TripCreateRequest request) {
        // 여행 계획을 데이터베이스에 저장하고, 고유한 ID를 생성합니다.
        return tripService.save(request);
    }

    @MessageMapping("/trip/edit/{planId}")
    @SendTo("/sub/trip/{planId}")
    public TripEditResponse editPlan(@DestinationVariable String planId, @Payload TripEditRequest request) {
        // 여행 계획을 데이터베이스에 업데이트하고, 업데이트된 계획을 반환합니다.
        return tripService.update(planId, request);
    }
}
