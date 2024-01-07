package org.tenten.tentenstomp.domain.trip.dto.request;

import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.util.List;

public record TripItemAddMsg(
    String visitDate,
    List<TripItemCreateRequest> newTripItems
) {
    public record TripItemCreateRequest(
        Long tourItemId,
        Transportation transportation,
        Long seqNum,
        Long price
    ) {

    }
}
