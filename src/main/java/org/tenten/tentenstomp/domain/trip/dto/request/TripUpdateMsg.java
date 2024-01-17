package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripUpdateMsg(
    String startDate,
    String endDate,
    Long numberOfPeople,
    String tripName,
    Long budget
) {
}
