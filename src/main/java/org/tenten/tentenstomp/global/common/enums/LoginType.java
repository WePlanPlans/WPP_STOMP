package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum LoginType {
    EMAIL(1L, "EMAIL"), KAKAO(2L, "KAKAO");

    private final Long id;
    private final String type;

    LoginType(Long id, String type) {
        this.id = id;
        this.type = type;
    }
}