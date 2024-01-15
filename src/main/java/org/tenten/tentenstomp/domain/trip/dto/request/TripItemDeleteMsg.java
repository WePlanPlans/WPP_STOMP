package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemDeleteMsg(
    Long tripId,
    String visitDate
) {
}
