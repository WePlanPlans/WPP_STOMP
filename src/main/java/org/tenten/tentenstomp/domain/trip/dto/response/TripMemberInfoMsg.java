package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripMemberInfoMsg(
    Long memberId,
    String name,
    String thumbnailUrl
) {
}
