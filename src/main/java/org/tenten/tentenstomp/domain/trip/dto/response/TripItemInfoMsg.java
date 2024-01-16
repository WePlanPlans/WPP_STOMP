package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripItemInfoMsg(
    Long tripItemId,
    Long tourItemId,
    String name,
    String thumbnailUrl,
    String category,
    Long seqNum,
    String visitDate,
    Long price
) {

}
