package org.tenten.tentenstomp.global.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.tenten.tentenstomp.global.common.constant.RedisConstant.CACHE_EXPIRE_TIME_MS;

@Component
@RequiredArgsConstructor
public class RedisCache {
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String topic, String key, Object data) {
        redisTemplate.opsForValue().set(topic+":"+key, data, CACHE_EXPIRE_TIME_MS, MILLISECONDS);
    }

    public void save(String topic, String key, String visitDate, Object data) {
        redisTemplate.opsForValue().set(topic + ":" + key + ":" + visitDate, data, CACHE_EXPIRE_TIME_MS, MILLISECONDS);
    }

    public Object get(String topic, String key) {
        Object cached = redisTemplate.opsForValue().get(topic + ":" + key);
        if (cached != null) {
            redisTemplate.expire(topic + ":" + key, CACHE_EXPIRE_TIME_MS, MILLISECONDS);
        }
        return cached;
    }

    public Object get(String topic, String key, String visitDate) {
        Object cached = redisTemplate.opsForValue().get(topic + ":" + key + ":" + visitDate);
        if (cached != null) {
            redisTemplate.expire(topic + ":" + key + ":" + visitDate, CACHE_EXPIRE_TIME_MS, MILLISECONDS);
        }
        return cached;
    }

    public void delete(String topic, String key) {
        redisTemplate.delete(topic +":"+key);
    }

    public void delete(String topic, String key, String visitDate) {
        redisTemplate.delete(topic + ":" + key + ":" + visitDate);
    }
}
