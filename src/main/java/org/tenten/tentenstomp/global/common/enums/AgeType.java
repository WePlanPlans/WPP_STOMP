package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;

@Getter
public enum AgeType {
    TEENAGER("십대"),
    TWENTIES("이십대"),
    THIRTIES("삼십대"),
    FORTIES("사십대"),
    ABOVE_FIFTIES("오십대"),
    DEFAULT("선택 안함");
    private final String name;

    AgeType(String name) {
        this.name = name;
    }
}
