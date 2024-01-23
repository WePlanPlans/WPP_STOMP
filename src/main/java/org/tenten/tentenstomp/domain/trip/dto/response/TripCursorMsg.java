package org.tenten.tentenstomp.domain.trip.dto.response;


public record TripCursorMsg(
    String tripId,
    String visitDate,
    Long memberId,
    String name,
    Double x,
    Double y
) {
}
