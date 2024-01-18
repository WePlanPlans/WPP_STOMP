package org.tenten.tentenstomp.global.component.dto.request;

import org.tenten.tentenstomp.domain.trip.entity.TripItem;

import java.util.List;

public record TripPlace(
    Long tripItemId,
    Long seqNum,
    String longitude,
    String latitude,
    Long price
) {
    public static List<TripPlace> fromTripItems(List<TripItem> tripItems) {

        return new java.util.ArrayList<>(tripItems.stream().map(t -> new TripPlace(t.getId(), t.getSeqNum(), t.getTourItem().getLongitude(), t.getTourItem().getLatitude(), t.getPrice())).toList());
    }
}
