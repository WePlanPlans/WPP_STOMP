package org.tenten.tentenstomp.domain.trip.dto.request;

import org.tenten.tentenstomp.domain.trip.dto.response.TripPlaceResponseMsg.TripPlace;

import java.util.List;

public record TripPlaceRequestMsg(List<TripPlace> places) {
}
