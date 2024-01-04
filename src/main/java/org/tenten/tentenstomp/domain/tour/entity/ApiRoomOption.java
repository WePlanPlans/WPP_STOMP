package org.tenten.tentenstomp.domain.tour.entity;

import lombok.*;
import org.tenten.tentenstomp.global.converter.JsonConverter;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRoomOption {
    String roomCode;
    String roomTitle;
    String roomSize; // 객실 크기 (평)
    String roomCount; // 객실수
    String basePeople; // 기준인원
    String maxPeople; // 최대인원
    String offSeasonWeekMin; // 비수기 주중 최소
    String offSeasonWeekendMin; // 비수기 주말 최소
    String peakSeasonWeekMin; // 성수기 주중 최소
    String peakSeasonWeekendMin; // 성수기 주말 최소
    String roomIntro; // 객실 소개
    String img1; // 사진 1
    String img1Description; // 사진1 설명
    String img2; // 사진 2
    String img2Description; // 사진2 설명
    String img3; // 사진 3
    String img3Description; // 사진3 설명
    String img4; // 사진 4
    String img4Description; // 사진4 설명
    String img5; // 사진 5
    String img5Description; // 사진5 설명

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiRoomOption that = (ApiRoomOption) o;
        return Objects.equals(roomCode, that.roomCode) && Objects.equals(roomTitle, that.roomTitle) && Objects.equals(roomSize, that.roomSize) && Objects.equals(roomCount, that.roomCount) && Objects.equals(basePeople, that.basePeople) && Objects.equals(maxPeople, that.maxPeople) && Objects.equals(offSeasonWeekMin, that.offSeasonWeekMin) && Objects.equals(offSeasonWeekendMin, that.offSeasonWeekendMin) && Objects.equals(peakSeasonWeekMin, that.peakSeasonWeekMin) && Objects.equals(peakSeasonWeekendMin, that.peakSeasonWeekendMin) && Objects.equals(roomIntro, that.roomIntro) && Objects.equals(img1, that.img1) && Objects.equals(img1Description, that.img1Description) && Objects.equals(img2, that.img2) && Objects.equals(img2Description, that.img2Description) && Objects.equals(img3, that.img3) && Objects.equals(img3Description, that.img3Description) && Objects.equals(img4, that.img4) && Objects.equals(img4Description, that.img4Description) && Objects.equals(img5, that.img5) && Objects.equals(img5Description, that.img5Description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomCode, roomTitle, roomSize, roomCount, basePeople, maxPeople, offSeasonWeekMin, offSeasonWeekendMin, peakSeasonWeekMin, peakSeasonWeekendMin, roomIntro, img1, img1Description, img2, img2Description, img3, img3Description, img4, img4Description, img5, img5Description);
    }

    public static class ApiRoomOptionConverter extends JsonConverter<ApiRoomOption> {

    }
}