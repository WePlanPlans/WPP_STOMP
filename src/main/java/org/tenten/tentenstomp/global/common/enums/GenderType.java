package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum GenderType {
    MALE("남성"),
    FEMALE("여성"),
    NON_BINARY("해당 없음"),
    DEFAULT("선택 안함");
    private final String name;

    GenderType(String name) {
        this.name = name;
    }
}