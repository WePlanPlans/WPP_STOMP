package org.tenten.tentenstomp.domain.trip.dto.request;

import java.util.List;

public record TripItemOrderUpdateMsg(
    String visitDate,
    List<OrderInfo> tripItemOrder

) {
    public record OrderInfo(
        Long tripItemId,
        Long seqNum
    ){}
}
