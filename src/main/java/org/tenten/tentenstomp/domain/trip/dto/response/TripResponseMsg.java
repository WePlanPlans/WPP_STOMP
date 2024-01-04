package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripResponseMsg(
    Long tripId,
    String visitDate,
    String endPoint,
    TripInfoResponseMsg tripInfoMessage,
    TripMemberResponseMsg tripMemberMessage,
    TripPlaceResponseMsg tripPlaceMessage) {
}
