package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.domain.member.entity.Survey;

import java.util.List;
import java.util.Map;

public record TripMemberResponseMsg(
    List<ConnectMemberMessage> connectedMembers, // 논의후 삭제 가능, 왜냐면, ws만 쓸때는 커넥션을 맺을 때, 해제할 때 호출되는 메소드가 있었는데, 현재 구조에서는 확실치 않음,
    TripFavor tripFavor,
    List<TripLikedItem> tripLikedItems
) {
    public record ConnectMemberMessage(
        Long memberId,
        String name,
        String thumbnailUrl
    ) {

    }

    public record TripFavor(
        List<MemberSurveyResult> memberSurveyResults,
        MemberFavorStatistic favorStatistic

    ) {

        public record MemberSurveyResult(
            Long memberId,
            Survey survey
        ) {

        }

        public record MemberFavorStatistic(
            Map<String, Long> planning,
            Map<String, Long> activeHours,
            Map<String, Long> accommodations,
            Map<String, Long> foods,
            Map<String, Long> tripStyle
        ) {

        }
    }

    public record TripLikedItem(
        Long tripLikedItemId,
        String thumbnailImageUrl,
        String tourItemName,
        Long tourItemId,
        List<TripLikedItemPreference> tripLikedItemPreferences
    ) {
        public record TripLikedItemPreference(
            Long memberId,
            Boolean liked,
            Boolean disliked
        ) {

        }

    }
}
