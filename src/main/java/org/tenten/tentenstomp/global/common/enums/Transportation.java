package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum Transportation {
    CAR("CAR"), PUBLIC_TRANSPORTATION("PUBLIC_TRANSPORTATION");

    private final String name;

    Transportation(String name) {
        this.name = name;
    }
}
