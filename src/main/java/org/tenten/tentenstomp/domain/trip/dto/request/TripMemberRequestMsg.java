package org.tenten.tentenstomp.domain.trip.dto.request;

import org.tenten.tentenstomp.domain.trip.dto.response.TripMemberResponseMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripMemberResponseMsg.TripLikedItem.TripLikedItemPreference;

import java.util.List;

public record TripMemberRequestMsg(
    List<TripLikedItemPreference> tripLikedItemPreferences
) {
}
