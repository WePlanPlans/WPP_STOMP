package org.tenten.tentenstomp.global.util;

import org.tenten.tentenstomp.domain.trip.entity.TripItem;

import java.util.List;

public class SequenceUtil {
    public static void updateSeqNum(List<TripItem> tripItems) {
        for (int i = 0; i < tripItems.size(); i++) {
            TripItem tripItem = tripItems.get(i);
            tripItem.updateSeqNum(i + 1L);
        }
    }
}
