package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.global.common.enums.TripStatus;

import java.time.LocalDate;

import static org.tenten.tentenstomp.global.common.enums.TripStatus.*;

public record TripInfoMsg(
    String tripId,
    String startDate,
    String endDate,
    Long numberOfPeople,
    String tripName,
    TripStatus tripStatus,
    Long budget
) {
    public static TripInfoMsg fromEntity(Trip trip) {
        LocalDate now = LocalDate.now();
        TripStatus tripStatus;
        if (now.isBefore(trip.getStartDate())) {
            tripStatus = BEFORE;
        } else if (now.isAfter(trip.getEndDate())) {
            tripStatus = AFTER;
        } else {
            tripStatus = ING;
        }
        return new TripInfoMsg(trip.getEncryptedId(), trip.getStartDate().toString(), trip.getEndDate().toString(), trip.getNumberOfPeople(), trip.getTripName(), tripStatus, trip.getBudget());
    }
}
