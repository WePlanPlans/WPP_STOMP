package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemPriceUpdateMsg(
    Long tripId,
    String visitDate,
    Long price
) {
}
