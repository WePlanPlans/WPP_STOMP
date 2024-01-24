package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.domain.trip.entity.Trip;

import java.util.List;

public record TripMemberMsg(
    String tripId,
    List<TripMemberInfoMsg> tripMembers,
    Long numberOfPeople
) {

    public static TripMemberMsg fromEntity(Trip trip, List<TripMemberInfoMsg> tripMemberInfoMsgs) {
        return new TripMemberMsg(
            trip.getEncryptedId(),
            tripMemberInfoMsgs,
            trip.getNumberOfPeople()
        );
    }
}
