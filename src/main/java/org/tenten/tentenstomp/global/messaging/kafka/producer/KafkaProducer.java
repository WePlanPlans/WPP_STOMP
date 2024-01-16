package org.tenten.tentenstomp.global.messaging.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.tenten.tentenstomp.domain.trip.dto.response.*;
import org.tenten.tentenstomp.global.cache.RedisCache;

import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisCache redisCache;

    public void send(String to, Object data) {
        kafkaTemplate.send(to, data);
    }

    public void sendWithOutCaching(Object... dataArgs) {
        for (Object data : dataArgs) {
            if (data.getClass().equals(TripPathMsg.class)) {
                send(PATH, data);
            }
            if (data.getClass().equals(TripItemMsg.class)) {
                send(TRIP_ITEM, data);
            }
            if (data.getClass().equals(TripInfoMsg.class)) {
                send(TRIP_INFO, data);
            }
            if (data.getClass().equals(TripMemberMsg.class)) {
                send(MEMBER, data);
            }
            if (data.getClass().equals(TripBudgetMsg.class)) {
                send(BUDGET, data);
            }
        }
    }

    public void sendAndSaveToRedis(Object... dataArgs) {
        for (Object data : dataArgs) {
            if (data.getClass().equals(TripPathMsg.class)) {
                send(PATH, data);
                TripPathMsg tripPathMsg = (TripPathMsg) data;
                redisCache.save(PATH, Long.toString(tripPathMsg.tripId()), tripPathMsg.visitDate(), tripPathMsg);
            }
            if (data.getClass().equals(TripItemMsg.class)) {
                send(TRIP_ITEM, data);
                TripItemMsg tripItemMsg = (TripItemMsg) data;
                redisCache.save(TRIP_ITEM, Long.toString(tripItemMsg.tripId()), tripItemMsg.visitDate(), tripItemMsg);
            }
            if (data.getClass().equals(TripInfoMsg.class)) {
                send(TRIP_INFO, data);
                TripInfoMsg tripInfoMsg = (TripInfoMsg) data;
                redisCache.save(TRIP_INFO, Long.toString(tripInfoMsg.tripId()), tripInfoMsg);
            }
            if (data.getClass().equals(TripMemberMsg.class)) {
                send(MEMBER, data);
                TripMemberMsg tripMemberMsg = (TripMemberMsg) data;
                redisCache.save(MEMBER, Long.toString(tripMemberMsg.tripId()), tripMemberMsg);
            }
            if (data.getClass().equals(TripBudgetMsg.class)) {
                send(BUDGET, data);
                TripBudgetMsg tripBudgetMsg = (TripBudgetMsg) data;
                redisCache.save(BUDGET, Long.toString(tripBudgetMsg.tripId()), tripBudgetMsg);
            }
        }
    }

}
