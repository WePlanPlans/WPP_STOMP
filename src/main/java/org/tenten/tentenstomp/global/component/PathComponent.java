package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.global.component.dto.request.TripPlaceUpdateRequest.TripPlaceInfo;
import org.tenten.tentenstomp.global.component.dto.response.PathInfo;
import org.tenten.tentenstomp.global.component.dto.response.TripPathInfo;

@Component
@RequiredArgsConstructor
public class PathComponent {
    private final OdsayComponent odsayComponent;
    private final NaverMapComponent naverMapComponent;

    public TripPathInfo calculatePathByCar(TripPlaceInfo fromPlace, TripPlaceInfo toPlace) {
        PathInfo pathInfo = naverMapComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        return new TripPathInfo(fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(), toPlace.transportation(), pathInfo);
    }

    public TripPathInfo calculatePathByPublicTransportation(TripPlaceInfo fromPlace, TripPlaceInfo toPlace) {
        PathInfo pathInfo = odsayComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        return new TripPathInfo(fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(), toPlace.transportation(), pathInfo);
    }
}
