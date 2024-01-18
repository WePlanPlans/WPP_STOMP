package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;
import org.tenten.tentenstomp.global.common.enums.Transportation;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;

import java.util.List;

import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncPathComponent {
    private final OdsayComponent odsayComponent;
    private final NaverMapComponent naverMapComponent;

    @Async("pathTaskExecutor")
    public void calculatePath(TripPlace fromPlace, TripPlace toPlace, List<TripPathInfoMsg> pathInfoMsgs, Transportation transportation) {

        long startTime = System.currentTimeMillis();
        TripPathInfoMsg.PathInfo pathInfo;
        if (transportation.equals(CAR)) {
            pathInfo = naverMapComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        } else {
            pathInfo = odsayComponent.calculatePathInfo(fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude());
        }
        log.info("from id "+fromPlace.tripItemId()+" to id "+toPlace.tripItemId()+" from seqNum " + fromPlace.seqNum() + " to seqNum " + toPlace.seqNum() + " executionTime : " + ((System.currentTimeMillis() - startTime) / 1000.0));
        pathInfoMsgs.add(new TripPathInfoMsg(fromPlace.tripItemId(), toPlace.tripItemId(), fromPlace.seqNum(), toPlace.seqNum(), fromPlace.longitude(), fromPlace.latitude(), toPlace.longitude(), toPlace.latitude(),  pathInfo));
    }
}
