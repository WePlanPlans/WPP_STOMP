package org.tenten.tentenstomp.domain.trip.dto.response;

import java.time.LocalDate;

public record TripItemInfo(
    Long tripItemId,
    Long tourItemId,
    String name,
    String thumbnailUrl,
    Long contentTypeId,
    Long seqNum,
    LocalDate visitDate,
    Long price
) {

}