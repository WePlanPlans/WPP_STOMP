package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;
import org.tenten.tentenstomp.global.exception.GlobalException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum Category {
    DINING(39L, "식당"),
    ACCOMMODATION(32L, "숙소"),
    ATTRACTION(12L, "관광지");

    private final Long code;
    private final String name;

    Category(Long code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Category fromCode(Long code) {
        for (Category category : Category.values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }

        throw new GlobalException("주어진 코드로 존재하는 카테고리가 없습니다.", NOT_FOUND);
    }

    public static Category fromName(String name) {
        for (Category category : Category.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }

        throw new GlobalException("주어진 코드로 존재하는 카테고리가 없습니다.", NOT_FOUND);
    }

}