package org.tenten.tentenstomp.global.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.global.common.annotation.WithRedissonLock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.tenten.tentenstomp.global.common.constant.ErrorMsgConstant.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UtilAspect {
    private final RedissonClient redissonClient;
    @Around("@annotation(org.tenten.tentenstomp.global.common.annotation.GetExecutionTime)")
    public Object getExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        log.info(joinPoint.getSignature().getName() + " execution time : " + ((System.currentTimeMillis() - startTime) / 1000.0));

        return proceed;
    }

    @Around("@annotation(org.tenten.tentenstomp.global.common.annotation.WithRedissonLock)")
    public Object withRedissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        WithRedissonLock withRedissonLock = method.getAnnotation(WithRedissonLock.class);
        String tripId = getTripId(
            joinPoint.getArgs(),
            withRedissonLock.paramClassType(),
            withRedissonLock.identifier(),
            signature.getParameterNames()
        );

        RLock lock = redissonClient.getLock(tripId);

        long waitTime = withRedissonLock.waitTime();
        long leaseTime = withRedissonLock.leaseTime();
        TimeUnit timeUnit = withRedissonLock.timeUnit();
        try {
            boolean available = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) {
                throw new RuntimeException(REDISSON_TIME_OUT);
            }
            log.info("redisson lock acquired " + tripId + " thread " + Thread.currentThread().getId());
            return joinPoint.proceed(joinPoint.getArgs());
        } finally {
            lock.unlock();
        }
    }

    private String getTripId(Object[] args, Class<?> paramClassType, String identifier, String[] parameterNames) {
        String tripId;
        if (paramClassType.equals(Object.class)) {
            tripId = getFromPrimitive(parameterNames, args, identifier);
        } else {
            tripId = getFromObject(args, paramClassType, identifier);
        }
        return tripId;
    }

    private String getFromObject(Object[] args, Class<?> paramClassType, String identifier) {
        for (Object object : args) {
            if (object.getClass().equals(paramClassType)) {
                Class<?> aClass = object.getClass();

                Object result;
                try {
                    result = aClass.getMethod(identifier).invoke(object);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return result.toString();
            }
        }
        throw new RuntimeException(INVALID_OBJECT_TYPE);
    }

    private String getFromPrimitive(String[] parameterNames, Object[] args, String identifier) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(identifier)) {
                return String.valueOf(args[i]);
            }
        }
        throw new RuntimeException(INVALID_PARAM);
    }

}