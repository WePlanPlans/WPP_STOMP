package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripMemberInfoMsg(
    Long memberId,
    String name,
    String thumbnailUrl,
    Boolean connected
) {
    public record TripMemberInfo(
        Long memberId,
        String name,
        String thumbnailUrl
    ) {

    }
}
