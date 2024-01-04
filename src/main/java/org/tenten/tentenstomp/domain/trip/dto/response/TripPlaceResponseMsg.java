package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;
import org.tenten.tentenstomp.global.component.dto.response.PathInfo;

import java.util.List;

public record TripPlaceResponseMsg(
    List<TripPlace> places,
    List<TripPath> paths
) {
    public record TripPlace(
        Long tripItemId,
        Long seqNum,
        Transportation transportation,
        String visitDate,
        Long budget,
        String longitude,
        String latitude

    ) {

    }

    public record TripPath(
        Long fromSeqNum,
        Long toSeqNum,
        String fromLongitude,
        String fromLatitude,
        String toLongitude,
        String toLatitude,
        Transportation transportation,
        PathInfo pathInfo
    ) {

    }
}