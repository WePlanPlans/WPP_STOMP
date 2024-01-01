package org.tenten.tentenstomp.global.component.dto.request;

import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.time.LocalDate;
import java.util.List;

public record TripPlaceUpdateRequest(List<TripPlaceInfo> places) {
    public record TripPlaceInfo(
        Long tripItemId,
        Long seqNum,
        Transportation transportation,
        LocalDate visitDate,
        Long estimatePrice,
        String latitude,
        String longitude) {}
}
