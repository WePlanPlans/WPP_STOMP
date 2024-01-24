package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripItemAddResponse(
    String tripId,
    Long tripItemId,
    Long tourItemId,
    String visitDate
) {
}
