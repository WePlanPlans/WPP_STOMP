package org.tenten.tentenstomp.domain.trip.dto.request;

import org.tenten.tentenstomp.global.common.enums.Transportation;

public record TripItemTransportationUpdateMsg(
    Long tripId,
    String visitDate,
    Transportation transportation
) {
}
