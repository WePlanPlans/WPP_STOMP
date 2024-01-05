package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.global.publisher.RedisPublisher;
import org.tenten.tentenstomp.global.util.RedisChannelUtil;

import static org.tenten.tentenstomp.global.common.constant.EndPointConstant.TRIP_ITEM;

@Service
@RequiredArgsConstructor
public class TripItemService {
    private final TripItemRepository tripItemRepository;
    private final RedisChannelUtil redisChannelUtil;
    private final RedisPublisher redisPublisher;
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
