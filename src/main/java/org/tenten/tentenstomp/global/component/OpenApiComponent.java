package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.tenten.tentenstomp.global.component.dto.response.AreaOpenApiResponse;
import org.tenten.tentenstomp.global.component.dto.response.RegionInfo;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenApiComponent {
    @Value("${open-api.url}")
    private String apiUrl;
    @Value("${open-api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;
    private static final String AREA = "/areaCode1";


    public List<RegionInfo> getSubRegion(String areaCode) {
        UriComponents uri = UriComponentsBuilder
            .fromUriString(apiUrl + AREA)
            .queryParam("serviceKey", apiKey)
            .queryParam("numOfRows", "100")
            .queryParam("pageNo", "1")
            .queryParam("_type", "json")
            .queryParam("MobileOS", "ETC")
            .queryParam("MobileApp", "TestApp")
            .queryParam("areaCode", areaCode)
            .build();
        log.info(uri.toUriString());
        ResponseEntity<AreaOpenApiResponse> apiResponseEntity = restTemplate.getForEntity(uri.toUriString(), AreaOpenApiResponse.class);
        AreaOpenApiResponse apiResponse = apiResponseEntity.getBody();
        return null;
//        return apiResponse.getResponse().getBody().getItems().getItem().stream().map(areaResponse -> new RegionInfo(Long.parseLong(areaCode), Long.parseLong(areaResponse.getCode()), areaResponse.getName())).toList();
    }
}