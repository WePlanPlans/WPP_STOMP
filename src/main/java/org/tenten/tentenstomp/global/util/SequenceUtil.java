package org.tenten.tentenstomp.global.util;

import org.tenten.tentenstomp.domain.trip.entity.TripItem;

import java.util.List;

public class SequenceUtil {
    public static void updateSeqNum(List<TripItem> tripItems) {
        tripItems.sort((a, b) -> {
            if (!a.getSeqNum().equals(b.getSeqNum())) {

                return Integer.parseInt(Long.toString(a.getSeqNum() - b.getSeqNum()));
            }
            return Integer.parseInt(Long.toString(a.getId() - b.getId()));
        });
        for (int i = 0; i < tripItems.size(); i++) {
            TripItem tripItem = tripItems.get(i);
            tripItem.updateSeqNum(i + 1L);
        }
    }
}
