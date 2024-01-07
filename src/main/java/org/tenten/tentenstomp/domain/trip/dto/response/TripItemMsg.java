package org.tenten.tentenstomp.domain.trip.dto.response;

import java.time.LocalDate;
import java.util.List;

public record TripItemMsg(
    Long tripId,
    String visitDate,
    List<TripItemInfoMsg> tripItems
) {
}
