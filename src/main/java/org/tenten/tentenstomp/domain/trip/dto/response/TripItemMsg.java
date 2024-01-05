package org.tenten.tentenstomp.domain.trip.dto.response;

import java.util.List;

public record TripItemMsg(
    List<TripItemInfoMsg> tripItems
) {
}
