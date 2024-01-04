package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripRequestMsg(
    Long tripId,
    String visitDate,
    String endPoint,
    TripInfoRequestMsg tripInfoMessage,
    TripPlaceRequestMsg tripPlaceMessage,
    TripMemberRequestMsg tripMemberMessage
) {
}
