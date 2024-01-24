package org.tenten.tentenstomp.domain.trip.dto.request;

public record CursorUpdateMsg(
    String token,
    String visitDate,
    Double x,
    Double y
) {
}
