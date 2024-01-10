package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

public record TripPathInfoMsg(
    Long fromSeqNum,
    Long toSeqNum,
    String fromLongitude,
    String fromLatitude,
    String toLongitude,
    String toLatitude,
    Transportation transportation,
    PathInfo pathInfo

) {
    public record PathInfo(
        Integer price,
        Double totalDistance,
        Long totalTime
    ){}
}
