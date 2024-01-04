package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripResponseMsg(
    Long tripId,
    String visitDate,
    String endPoint,
    TripEditResponseMsg tripEditMessage,
    TripMemberResponseMsg tripMemberMessage,
    TripPlaceResponseMsg tripPlaceMessage) {
}
