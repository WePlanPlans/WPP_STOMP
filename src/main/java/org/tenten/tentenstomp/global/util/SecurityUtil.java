package org.tenten.tentenstomp.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.global.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final JwtTokenProvider jwtTokenProvider;
    public Long getMemberId(String accessToken) {
        String memberId = jwtTokenProvider.getMemberId(accessToken);
        if (memberId == null || memberId.equals("null") || memberId.equals("anonymousUser")) {
            return null;
        }

        return Long.parseLong(memberId);
    }
}
