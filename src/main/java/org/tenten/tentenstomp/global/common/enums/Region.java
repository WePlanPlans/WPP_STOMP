package org.tenten.tentenstomp.global.common.enums;

import lombok.Getter;
import org.tenten.tentenstomp.global.exception.GlobalException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum Region {
    SEOUL(1L, null, "서울"),
    INCHEON(2L, null, "인천"),
    DAEJEON(3L, null, "대전"),
    DAEGU(4L, null, "대구"),
    GWANGJU(5L, null, "광주"),
    BUSAN(6L, null, "부산"),
    ULSAN(7L, null, "울산"),
    SEJONG(8L, null, "세종특별자치시"),
    GYEONGGI(31L, null, "경기도"),
    GANGWON(32L, null, "강원도"),
    CHUNGBUK(33L, null, "충청북도"),
    CHUNGNAM(34L, null, "충청남도"),
    GYEONGBUK(35L, null, "경상북도"),
    GYEONGNAM(36L, null, "경상남도"),
    JEONBUK(37L, null, "전라북도"),
    JEONNAM(38L, null, "전라남도"),
    JEJU(39L, null, "제주도"),
    GYEONGJU(35L, 2L, "경주"),
    GANREUNG(32L, 1L, "강릉");
    private final Long areaCode;
    private final Long subAreaCode;
    private final String name;
    public static final List<Region> entireRegions = List.of(SEOUL, INCHEON, DAEJEON, DAEGU, GWANGJU, BUSAN, ULSAN, SEJONG, GYEONGGI, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, JEJU);
    public static final List<Region> popularPlaces = List.of(SEOUL, BUSAN, JEJU, GYEONGJU, GANREUNG);

    Region(Long areaCode, Long subAreaCode, String name) {
        this.areaCode = areaCode;
        this.subAreaCode = subAreaCode;
        this.name = name;
    }

    public static Region fromName(String name) {
        for (Region region : Region.values()) {
            if (region.getName().equals(name)) {
                return region;
            }
        }
        throw new GlobalException("주어진 이름으로 존재하는 지역이 없습니다.", NOT_FOUND);
    }

    public static Region fromAreaCode(Long areaCode) {
        for (Region region : Region.values()) {
            if (region.getAreaCode().equals(areaCode)) {

                return region;
            }
        }
        throw new GlobalException("주어진 지역 코드로 존재하는 지역이 없습니다.", NOT_FOUND);
    }
}