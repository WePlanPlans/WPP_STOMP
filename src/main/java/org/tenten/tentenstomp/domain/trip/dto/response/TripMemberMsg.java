package org.tenten.tentenstomp.domain.trip.dto.response;

import java.util.List;

public record TripMemberMsg(
    Long tripId,
    List<TripMemberInfoMsg> connectedMembers,
    List<TripMemberInfoMsg> tripMembers
) {
}
