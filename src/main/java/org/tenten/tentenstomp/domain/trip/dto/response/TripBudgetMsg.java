package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.domain.trip.entity.Trip;

public record TripBudgetMsg(
    String tripId,
    Long budget,
    Long calculatedPrice
) {
    public static TripBudgetMsg fromEntity(Trip trip) {
        return new TripBudgetMsg(
            trip.getEncryptedId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum()
        );
    }
}
