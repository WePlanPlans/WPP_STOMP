package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum TripStatus {
    BEFORE("여행 전"),
    AFTER("여행 후"),
    ING("여행 중");
    private final String name;

    TripStatus(String name) {
        this.name = name;
    }
}