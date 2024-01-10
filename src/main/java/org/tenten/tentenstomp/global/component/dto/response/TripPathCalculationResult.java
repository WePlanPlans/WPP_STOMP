package org.tenten.tentenstomp.global.component.dto.response;

import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;

import java.util.List;

public record TripPathCalculationResult(
    Integer pathPriceSum,
    List<TripPathInfoMsg> tripPathInfoMsgs
) {
}
