package org.tenten.tentenstomp.domain.trip.dto.response;

import java.time.LocalDate;
import java.util.List;

public record TripPathMsg(
    Long tripId,
    String visitDate,
    List<TripPathInfoMsg> paths
) {
}
