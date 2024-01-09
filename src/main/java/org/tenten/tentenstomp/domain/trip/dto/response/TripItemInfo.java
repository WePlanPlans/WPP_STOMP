package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.time.LocalDate;

public record TripItemInfo(
    Long tripItemId,
    Long tourItemId,
    String name,
    String thumbnailUrl,
    Long contentTypeId,
    Transportation transportation,
    Long seqNum,
    LocalDate visitDate,
    Long price
) {

}