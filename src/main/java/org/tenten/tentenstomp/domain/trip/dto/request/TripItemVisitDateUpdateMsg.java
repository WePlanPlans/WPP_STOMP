package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemVisitDateUpdateMsg(
    Long tripId,
    String oldVisitDate,
    String newVisitDate
) {
}
