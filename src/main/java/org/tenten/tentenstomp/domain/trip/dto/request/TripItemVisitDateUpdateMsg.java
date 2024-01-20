package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripItemVisitDateUpdateMsg(
    String tripId,
    String oldVisitDate,
    String newVisitDate
) {
}
