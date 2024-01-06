package org.tenten.tentenstomp.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class UtilAspect {

    @Around("@annotation(org.tenten.tentenstomp.global.common.annotation.GetExecutionTime)")
    public Object getExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        log.info(joinPoint.getSignature().getName() + " execution time : " + ((System.currentTimeMillis() - startTime) / 1000.0));

        return proceed;
    }
}