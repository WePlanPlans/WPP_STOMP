package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.util.List;

public record TripPathMsg(
    Long tripId,
    String visitDate,
    Transportation transportation,
    List<TripPathInfoMsg> paths
) {
}
