package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemDeleteMsg(
    String tripId,
    String visitDate
) {
}
