package org.tenten.tentenstomp.global.component.dto.response;

import org.tenten.tentenstomp.global.common.enums.Transportation;

public record PathInfo(
    Long totalTime,
    Double totalDistance,
    Long price
) {
}
