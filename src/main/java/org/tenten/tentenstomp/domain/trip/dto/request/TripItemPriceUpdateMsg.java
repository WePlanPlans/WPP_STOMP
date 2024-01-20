package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemPriceUpdateMsg(
    String tripId,
    String visitDate,
    Long price
) {
}
