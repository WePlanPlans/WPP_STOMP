package org.tenten.tentenstomp.domain.trip.dto.request;

public record TripRequestMsg(
    Long tripId,
    String visitDate,
    String endPoint,
    TripEditRequestMsg tripEditMessage,
    TripPlaceRequestMsg tripPlaceMessage,
    TripMemberRequestMsg tripMemberMessage
) {
}
