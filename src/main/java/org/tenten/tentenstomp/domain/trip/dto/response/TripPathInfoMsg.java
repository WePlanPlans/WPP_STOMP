package org.tenten.tentenstomp.domain.trip.dto.response;

public record TripPathInfoMsg(
    Long fromTripItemId,
    Long toTripItemId,
    Long fromSeqNum,
    Long toSeqNum,
    String fromLongitude,
    String fromLatitude,
    String toLongitude,
    String toLatitude,
    PathInfo pathInfo

) {
    public record PathInfo(
        Integer price,
        Double totalDistance,
        Long totalTime
    ){}
}
