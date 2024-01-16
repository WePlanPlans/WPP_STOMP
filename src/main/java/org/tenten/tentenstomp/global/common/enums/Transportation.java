package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum Transportation {
    CAR("CAR"), PUBLIC_TRANSPORTATION("PUBLIC_TRANSPORTATION");

    private final String name;

    Transportation(String name) {
        this.name = name;
    }

    public static Transportation fromName(String name) {
        for (Transportation t : Transportation.values()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
}
