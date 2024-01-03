package org.tenten.tentenstomp.global.component.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

public record TripPathInfo(
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
