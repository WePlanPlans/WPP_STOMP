package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripItemAddResponse(
    Long tripId,
    Long tripItemId,
    Long tourItemId,
    String visitDate
) {
}
