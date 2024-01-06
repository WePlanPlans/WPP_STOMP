package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;
import org.tenten.tentenstomp.global.messaging.redis.publisher.RedisPublisher;
import org.tenten.tentenstomp.global.util.RedisChannelUtil;

@Service
@RequiredArgsConstructor
public class TripItemService {
    private final TripItemRepository tripItemRepository;
    private final RedisChannelUtil redisChannelUtil;
    private final RedisPublisher redisPublisher;
    private final KafkaProducer kafkaProducer;
    @Transactional
    public void updateTripItemPrice(String tripItemId, TripItemPriceUpdateMsg priceUpdateMsg) {
        // TODO : /sub/{tripId}/tripItems/{visitDate}

    }
    @Transactional
    public void updateTripItemVisitDate(String tripItemId, TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        // TODO : /sub/{tripId}/tripItems/{oldVisitDate}
        // TODO : /sub/{tripId}/tripItems/{newVisitDate}
        // TODO : /sub/{tripId}/path/{oldVisitDate}
        // TODO : /sub/{tripId}/path/{newVisitDate}
    }
    @Transactional
    public void deleteTripItem(String tripItemId) {
        // TODO : /sub/{tripId}/tripItems/{visitDate}
        // TODO : /sub/{tripId}/path/{visitDate}
    }
}
