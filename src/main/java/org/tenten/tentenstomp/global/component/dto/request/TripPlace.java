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
        List<TripPlace> list = new java.util.ArrayList<>(tripItems.stream().map(t -> new TripPlace(t.getId(), t.getSeqNum(), t.getTourItem().getLongitude(), t.getTourItem().getLatitude(), t.getPrice())).toList());
        list.sort((a, b) -> {
            if (!a.seqNum().equals(b.seqNum())) {

                return Integer.parseInt(Long.toString(a.seqNum() - b.seqNum()));
            }
            return Integer.parseInt(Long.toString(a.tripItemId() - b.tripItemId()));
        });
        return list;
    }
}
