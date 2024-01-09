package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.time.LocalDate;

public record TripItemInfoMsg(
    Long tripItemId,
    Long tourItemId,
    String name,
    String thumbnailUrl,
    String category,
    Transportation transportation,
    Long seqNum,
    String visitDate,
    Long price
) {

}
