package org.tenten.tentenstomp.global.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonSameTransaction implements Transaction {
    @Transactional(propagation = MANDATORY, timeout = 3)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
