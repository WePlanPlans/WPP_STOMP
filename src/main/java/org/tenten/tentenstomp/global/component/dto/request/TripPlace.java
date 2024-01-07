package org.tenten.tentenstomp.global.component.dto.request;

import org.tenten.tentenstomp.global.common.enums.Transportation;

public record TripPlace(
    Long seqNum,
    Transportation transportation,
    String longitude,
    String latitude
) {
}
