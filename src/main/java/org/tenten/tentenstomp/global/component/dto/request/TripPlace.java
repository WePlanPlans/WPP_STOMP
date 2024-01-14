package org.tenten.tentenstomp.global.component.dto.request;

import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.util.List;

public record TripPlace(
    Long tripItemId,
    Long seqNum,
    Transportation transportation,
    String longitude,
    String latitude,
    Long price
) {
    public static List<TripPlace> fromTripItems(List<TripItem> tripItems) {
        return tripItems.stream().map(t -> new TripPlace(t.getId(), t.getSeqNum(), t.getTransportation(), t.getTourItem().getLongitude(), t.getTourItem().getLatitude(), t.getPrice())).toList();
    }
}
