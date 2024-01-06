package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.TripStatus;

public record TripInfoMsg(
    Long tripId,
    String startDate,
    String endDate,
    Long numberOfPeople,
    String tripName,
    TripStatus tripStatus,
    String area,
    String subarea,
    Long budget
) {
}
