package org.tenten.tentenstomp.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TopicUtil {
    private final String BASE_URL = "/sub";

    public String topicToReturnEndPoint(Long tripId, String endPoint, LocalDate visitDate) {
        return BASE_URL + "/" + tripId + "/" + endPoint + "/" + visitDate;
    }

    public String topicToReturnEndPoint(Long tripId, String endPoint) {
        return BASE_URL + "/" + tripId + "/" + endPoint;
    }
}
