package org.tenten.tentenstomp.domain.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemDeleteMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.service.TripItemService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TripItemController {
    private final TripItemService tripItemService;

    @MessageMapping("/tripItems/{tripItemId}/updatePrice")
    public void updateTripItemPrice(@DestinationVariable String tripItemId, @Payload TripItemPriceUpdateMsg priceUpdateMsg) {
        tripItemService.updateTripItemPrice(tripItemId, priceUpdateMsg);
    }

    @MessageMapping("/tripItems/{tripItemId}/updateVisitDate")
    public void updateTripItemVisitDate(@DestinationVariable String tripItemId, @Payload TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        tripItemService.updateTripItemVisitDate(tripItemId, visitDateUpdateMsg);
    }

    @MessageMapping("/tripItems/{tripItemId}/deleteItem")
    public void deleteTripItem(@DestinationVariable String tripItemId, @Payload TripItemDeleteMsg tripItemDeleteMsg) {
        tripItemService.deleteTripItem(tripItemId, tripItemDeleteMsg);
    }
}
