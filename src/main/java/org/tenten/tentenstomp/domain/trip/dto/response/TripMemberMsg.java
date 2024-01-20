package org.tenten.tentenstomp.domain.trip.dto.response;

import java.util.List;

public record TripMemberMsg(
    String tripId,
    List<TripMemberInfoMsg> tripMembers,
    Long numberOfPeople
) {
}
