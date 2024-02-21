package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg.PathInfo;
import org.tenten.tentenstomp.global.common.annotation.GetExecutionTime;
import org.tenten.tentenstomp.global.common.enums.Transportation;
import org.tenten.tentenstomp.global.component.dto.request.PathCalculateRequest;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PathComponent {
    private final AsyncPathComponent asyncPathComponent;


    @GetExecutionTime
    public TripPathCalculationResult getTripPath(List<TripPlace> tripPlaceList, Transportation transportation) {

        List<PathCalculateRequest> pathCalculateRequests = toPathCalculateRequest(tripPlaceList);
        if (pathCalculateRequests.isEmpty()) {
            List<TripPathInfoMsg> pathInfoMsgs = new ArrayList<>();
            if (!tripPlaceList.isEmpty()) {
                TripPlace tripPlace = tripPlaceList.get(0);
                pathInfoMsgs.add(new TripPathInfoMsg(
                    tripPlace.tripItemId(), tripPlace.tripItemId(), tripPlace.seqNum(), tripPlace.seqNum(),
                    tripPlace.longitude(), tripPlace.latitude(), tripPlace.longitude(), tripPlace.latitude(), new PathInfo(0, 0.0, 0L)
                ));
            }
            return new TripPathCalculationResult(0, pathInfoMsgs);

        } else {
            Integer priceSum = 0;
            List<TripPathInfoMsg> pathInfoMsgs = new ArrayList<>();
            for (PathCalculateRequest calculateRequest : pathCalculateRequests) {
                asyncPathComponent.calculatePath(calculateRequest.from(), calculateRequest.to(), pathInfoMsgs, transportation);
            }
            while (true) {
                if (pathInfoMsgs.size() == pathCalculateRequests.size()) {
                    break;
                }
            }
            for (TripPathInfoMsg tpm : pathInfoMsgs) {
                if (tpm.pathInfo().price() != -1) {
                    priceSum += tpm.pathInfo().price();
                }
            }
            pathInfoMsgs.sort((a, b) -> {
                if (!a.fromSeqNum().equals(b.fromSeqNum())) {

                    return Integer.parseInt(Long.toString(a.fromSeqNum() - b.fromSeqNum()));
                }
                return Integer.parseInt(Long.toString(a.fromTripItemId() - b.fromTripItemId()));
            });
            return new TripPathCalculationResult(priceSum, pathInfoMsgs);
        }


    }


    private List<PathCalculateRequest> toPathCalculateRequest(List<TripPlace> tripPlaceList) {
        List<PathCalculateRequest> pathCalculateRequests = new ArrayList<>();
        for (int i = 0; i + 1 < tripPlaceList.size(); i++) {
            pathCalculateRequests.add(new PathCalculateRequest(tripPlaceList.get(i), tripPlaceList.get(i + 1)));
        }
        return pathCalculateRequests;
    }
}
