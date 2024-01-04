package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.global.component.dto.request.TripPlaceUpdateRequest;
import org.tenten.tentenstomp.global.component.dto.request.TripPlaceUpdateRequest.TripPlaceInfo;
import org.tenten.tentenstomp.global.component.dto.response.PathInfo;

@Component
@RequiredArgsConstructor
public class PathComponent {
    private final OdsayComponent odsayComponent;
    private final NaverMapComponent naverMapComponent;

    public PathInfo calculatePathByCar(TripPlaceInfo fromPlace, TripPlaceInfo toPlace) {
        Long price = naverMapComponent.calculatePrice(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
//        return new PathInfo(fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(), toPlace.transportation(), price);
        return null;
    }

    public PathInfo calculatePathByPublicTransportation(TripPlaceInfo fromPlace, TripPlaceInfo toPlace) {
        Long price = odsayComponent.calculatePrice(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
//        return new PathInfo(fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(), toPlace.transportation(), price);
        return null;
    }
}
