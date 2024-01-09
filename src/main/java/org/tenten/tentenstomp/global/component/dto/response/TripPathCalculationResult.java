package org.tenten.tentenstomp.global.component.dto.response;

import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;

import java.util.List;

public record TripPathCalculationResult(
    Long pathPriceSum,
    List<TripPathInfoMsg> tripPathInfoMsgs
) {
}
