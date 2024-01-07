package org.tenten.tentenstomp.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathInfoMsg.PathInfo;
import org.tenten.tentenstomp.global.exception.GlobalException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.CONFLICT;

@Component
@Slf4j
@RequiredArgsConstructor
public class OdsayComponent {
    private final RestTemplate restTemplate;
    @Value("${odsay.key}")
    private String apiKey;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";
    public PathInfo calculatePathInfo(String fromLongitude,
                                                      String fromLatitude,
                                                      String toLongitude,
                                                      String toLatitude) {
        UriComponents uri = UriComponentsBuilder
            .fromUriString(BASE_URL)
            .queryParam("apiKey", apiKey)
            .queryParam("SX", fromLongitude)
            .queryParam("SY", fromLatitude)
            .queryParam("EX", toLongitude)
            .queryParam("EY", toLatitude)
            .build();
//        log.info(uri.toUriString());
        HttpHeaders header = new HttpHeaders();
        HttpEntity request = new HttpEntity(header);
        ResponseEntity<String> response = restTemplate.exchange(uri.toUri(), GET, request, String.class);
        try {
            Map<String, Object> map = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
            });
            if (map.containsKey("result")) {
                Map<String, Object> resultMap = (Map<String, Object>) map.get("result");
                List<Map<String, Object>> pathList = (List<Map<String, Object>>) resultMap.get("path");
                Map<String, Object> path = pathList.get(0);
                Map<String, Object> pathInfo = (Map<String, Object>) path.get("info");
                Integer payment = (Integer) pathInfo.get("payment");
                Double distance = (Double) pathInfo.get("totalDistance");
                Integer totalTime = (Integer) pathInfo.get("totalTime");
                return new PathInfo((long) totalTime, distance, (long) payment);
            } else {
                throw new GlobalException("대중교통 경로를 조회할 수 없습니다.", CONFLICT);
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.info("대중교통 경로 응답 파일을 읽어오는 과정에서 오류가 발생했습니다.");
            return null;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
