package org.tenten.tentenstomp.global.lock;

import org.aspectj.lang.ProceedingJoinPoint;

public interface Transaction {
    Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable;
}
