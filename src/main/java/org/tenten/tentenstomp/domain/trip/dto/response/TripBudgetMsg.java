package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripBudgetMsg(
    Long tripId,
    Long budget,
    Long calculatedPrice
) {
}
