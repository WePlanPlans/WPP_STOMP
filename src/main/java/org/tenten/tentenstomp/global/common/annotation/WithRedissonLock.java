package org.tenten.tentenstomp.global.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithRedissonLock {
    String identifier() default "tripId";
    Class<?> paramClassType() default Object.class;

    long waitTime() default 5L;

    long leaseTime() default 4L;
    TimeUnit timeUnit() default SECONDS;
    boolean needSameTransaction() default false;
}
