package org.tenten.tentenstomp.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.tenten.tentenstomp.global.component.dto.response.PathInfo;
import org.tenten.tentenstomp.global.exception.GlobalException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.tenten.tentenstomp.global.common.constant.NaverMapConstant.NAVER_MAP_API_KEY_HEADER;
import static org.tenten.tentenstomp.global.common.constant.NaverMapConstant.NAVER_MAP_CLIENT_ID_HEADER;

@Component
@Slf4j
@RequiredArgsConstructor
public class NaverMapComponent {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving";
    @Value("${naver-map.key}")
    private String apiKey;
    @Value("${naver-map.client_id}")
    private String clientId;
    public PathInfo calculatePathInfo(String fromLongitude,
                                      String fromLatitude,
                                      String toLongitude,
                                      String toLatitude) {
        UriComponents uri = UriComponentsBuilder
            .fromUriString(BASE_URL)
            .queryParam("start", fromLongitude + "," + fromLatitude)
            .queryParam("goal",toLongitude+","+toLatitude)
            .build();
        log.info(uri.toUriString());
        HttpHeaders header = new HttpHeaders();
        header.set(NAVER_MAP_CLIENT_ID_HEADER, clientId);
        header.set(NAVER_MAP_API_KEY_HEADER, apiKey);
        HttpEntity request = new HttpEntity(header);
        ResponseEntity<String> response = restTemplate.exchange(uri.toUri(), GET, request, String.class);
        try {
            Map<String, Object> map = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
            });
            for (String key : map.keySet()) {
                log.info("key : " + key);
            }
            log.info("code : " + map.get("code"));
            log.info("msg : " + map.get("message"));
            if (map.get("code").equals(0)) {
                Map<String, Object> routeMap = (Map<String, Object>) map.get("route");
                List<Map<String, Object>> traoptimalList = (List<Map<String, Object>> ) routeMap.get("traoptimal");
                Map<String, Object> traoptimal = traoptimalList.get(0);
                for (String traKey : traoptimal.keySet()) {
                    log.info("tra key : "+traKey);
                }
                Map<String, Object> summary = (Map<String, Object>) traoptimal.get("summary");
                Integer tollFare = (Integer) summary.get("tollFare");
                Integer fuelPrice = (Integer) summary.get("fuelPrice");
                Integer distance = (Integer) summary.get("distance");
                Integer duration = (Integer) summary.get("duration");
                return new PathInfo((long) duration / 60_000 , (double) distance, (long)tollFare + fuelPrice);
            } else {
                throw new GlobalException("자동차 경로를 조회할 수 없습니다.", CONFLICT);
            }
        } catch (JsonProcessingException e) {
            throw new GlobalException("자동차 경로 응답 파일을 읽어오는 과정에서 오류가 발생했습니다.", CONFLICT);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
