package org.tenten.tentenstomp.domain.trip.dto.response;

import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.global.common.enums.Category;
import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.util.ArrayList;
import java.util.List;

public record TripItemMsg(
    String tripId,
    String visitDate,
    Transportation transportation,
    List<TripItemInfoMsg> tripItems
) {
    public static TripItemMsg fromTripItemList(String tripId, String visitDate, Transportation transportation, List<TripItem> tripItems) {

        List<TripItemInfoMsg> tripItemInfoMsgs = new ArrayList<>(tripItems.stream().map(t -> new TripItemInfoMsg(t.getId(), t.getTourItem().getId(), t.getTourItem().getTitle(), t.getTourItem().getOriginalThumbnailUrl(), Category.fromCode(t.getTourItem().getContentTypeId()).getName(), t.getSeqNum(), t.getVisitDate().toString(), t.getPrice())).toList());

        return new TripItemMsg(tripId, visitDate, transportation, tripItemInfoMsgs);
    }


    public static TripItemMsg fromTripItemList(String tripId, String visitDate, List<TripItem> tripItems, Long tripItemId, Transportation transportation, TripItemPriceUpdateMsg updateMsg) {
        List<TripItemInfoMsg> tripItemInfoMsgs = new ArrayList<>();
        for (TripItem t : tripItems) {
            if (t.getId().equals(tripItemId)) {
                tripItemInfoMsgs.add(new TripItemInfoMsg(t.getId(), t.getTourItem().getId(), t.getTourItem().getTitle(), t.getTourItem().getOriginalThumbnailUrl(), Category.fromCode(t.getTourItem().getContentTypeId()).getName(),  t.getSeqNum(), t.getVisitDate().toString(), updateMsg.price()));
            } else {
                tripItemInfoMsgs.add(new TripItemInfoMsg(t.getId(), t.getTourItem().getId(), t.getTourItem().getTitle(), t.getTourItem().getOriginalThumbnailUrl(), Category.fromCode(t.getTourItem().getContentTypeId()).getName(),  t.getSeqNum(), t.getVisitDate().toString(), t.getPrice()));
            }
        }

        return new TripItemMsg(tripId, visitDate, transportation, tripItemInfoMsgs );
    }
}
