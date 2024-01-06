package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg.PathInfo;
import org.tenten.tentenstomp.global.common.annotation.GetExecutionTime;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.request.PathCalculateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;

@Component
@RequiredArgsConstructor
@Slf4j
public class PathComponent {
    private final OdsayComponent odsayComponent;
    private final NaverMapComponent naverMapComponent;
    @Async
    public TripPathInfoMsg calculatePath(TripPlace fromPlace, TripPlace toPlace) {
        long startTime = System.currentTimeMillis();
        PathInfo pathInfo;
        if (toPlace.transportation().equals(CAR)) {
            pathInfo = naverMapComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        } else {
            pathInfo = odsayComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        }
        log.info("from "+fromPlace.seqNum()+" to "+toPlace.seqNum()+" executionTime : "+((System.currentTimeMillis() - startTime) / 1000.0));
        return new TripPathInfoMsg(fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(), toPlace.transportation(), pathInfo);
    }
    @GetExecutionTime
    public List<TripPathInfoMsg> getTripPath(List<TripPlace> tripPlaceList) {
        List<PathCalculateRequest> pathCalculateRequests = toPathCalculateRequest(tripPlaceList);
        return pathCalculateRequests.stream().flatMap(pathCalculateRequest -> Stream.of(calculatePath(pathCalculateRequest.from(), pathCalculateRequest.to()))).toList();
    }


    private List<PathCalculateRequest> toPathCalculateRequest(List<TripPlace> tripPlaceList) {
        List<PathCalculateRequest> pathCalculateRequests = new ArrayList<>();
        for (int i = 0; i + 1 < tripPlaceList.size(); i++) {
            pathCalculateRequests.add(new PathCalculateRequest(tripPlaceList.get(i), tripPlaceList.get(i + 1)));
        }
        return pathCalculateRequests;
    }
}
