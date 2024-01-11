package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;
import org.tenten.tentenstomp.global.common.annotation.GetExecutionTime;
import org.tenten.tentenstomp.global.component.dto.request.PathCalculateRequest;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class PathComponent {
    private final AsyncPathComponent asyncPathComponent;


    @GetExecutionTime
    public TripPathCalculationResult getTripPath(List<TripPlace> tripPlaceList) {
        List<PathCalculateRequest> pathCalculateRequests = toPathCalculateRequest(tripPlaceList);
        Integer priceSum = 0;
        List<TripPathInfoMsg> pathInfoMsgs = new CopyOnWriteArrayList<>();
        for (PathCalculateRequest calculateRequest : pathCalculateRequests) {
            asyncPathComponent.calculatePath(calculateRequest.from(), calculateRequest.to(), pathInfoMsgs);
        }
        while (true) {
            if (pathInfoMsgs.size() == pathCalculateRequests.size()) {
                break;
            }
        }
        for (TripPathInfoMsg tpm : pathInfoMsgs) {
            if (tpm.pathInfo() != null) {
                priceSum += tpm.pathInfo().price();
            }
        }
        return new TripPathCalculationResult(priceSum, pathInfoMsgs);

    }


    private List<PathCalculateRequest> toPathCalculateRequest(List<TripPlace> tripPlaceList) {
        List<PathCalculateRequest> pathCalculateRequests = new ArrayList<>();
        for (int i = 0; i + 1 < tripPlaceList.size(); i++) {
            pathCalculateRequests.add(new PathCalculateRequest(tripPlaceList.get(i), tripPlaceList.get(i + 1)));
        }
        return pathCalculateRequests;
    }
}
