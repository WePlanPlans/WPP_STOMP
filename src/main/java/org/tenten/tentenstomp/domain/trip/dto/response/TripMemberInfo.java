package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripMemberInfo(
    Long memberId,
    String name,
    String thumbnailUrl
) {

}