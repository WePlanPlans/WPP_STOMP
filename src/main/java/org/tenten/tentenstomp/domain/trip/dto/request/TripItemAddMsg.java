package org.tenten.tentenstomp.domain.trip.dto.request;

import org.tenten.tentenstomp.domain.tour.entity.TourItem;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.time.LocalDate;
import java.util.List;

import static org.tenten.tentenstomp.global.common.enums.Transportation.PUBLIC_TRANSPORTATION;

public record TripItemAddMsg(
    String visitDate,
    List<TripItemCreateRequest> newTripItems
) {
    public record TripItemCreateRequest(
        Long tourItemId
    ) {
        public static TripItem toEntity(TourItem tourItem,  Trip trip, Long seqNum, LocalDate visitDate) {

            return TripItem.builder()
                .transportation(PUBLIC_TRANSPORTATION)
                .trip(trip)
                .price(0L)
                .seqNum(seqNum)
                .tourItem(tourItem)
                .visitDate(visitDate)
                .build();
        }
    }
}
