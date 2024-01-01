package org.tenten.tentenstomp.global.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OdsayComponent {
    private final RestTemplate restTemplate;
    @Value("${odsay.key}")
    private String apiKey;
    private final String BASE_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";
    public Long calculatePrice(String fromLongitude,
                               String fromLatitude,
                               String toLongitude,
                               String toLatitude) {
        return null;
    }
}
