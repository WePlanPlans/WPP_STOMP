package org.tenten.tentenstomp.global.component.dto.request;

import org.tenten.tentenstomp.global.component.dto.request.TripPlaceUpdateRequest.TripPlaceInfo;

public record TempPathCalculateRequest(TripPlaceInfo from,
                                       TripPlaceInfo to) {

}
