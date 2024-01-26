package org.tenten.tentenstomp.global.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonTransactionFactory {
    private final RedissonNewTransaction redissonNewTransaction;
    private final RedissonSameTransaction redissonSameTransaction;

    public Transaction getTransaction(boolean same) {
        if (same) {
            return redissonSameTransaction;
        }
        return redissonNewTransaction;
    }
}
