package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripBudgetMsg(
    String tripId,
    Long budget,
    Long calculatedPrice
) {
}
