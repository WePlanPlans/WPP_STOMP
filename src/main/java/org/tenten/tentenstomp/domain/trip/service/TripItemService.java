package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemTransportationUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathMsg;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;
import org.tenten.tentenstomp.global.messaging.redis.publisher.RedisPublisher;
import org.tenten.tentenstomp.global.util.RedisChannelUtil;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.PATH;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.TRIP_ITEM;

@Service
@RequiredArgsConstructor
public class TripItemService {
    private final TripItemRepository tripItemRepository;
    private final RedisChannelUtil redisChannelUtil;
    private final RedisPublisher redisPublisher;
    private final KafkaProducer kafkaProducer;
    @Transactional
    public void updateTripItemPrice(String tripItemId, TripItemPriceUpdateMsg priceUpdateMsg) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        /*
        비즈니스 로직
         */
        TripItemMsg tripItemMsg = new TripItemMsg(
            tripItem.getTourItem().getId(), tripItem.getVisitDate().toString(), null
        );

        kafkaProducer.send(TRIP_ITEM, tripItemMsg);
        // TODO : budget
    }
    @Transactional
    public void updateTripItemVisitDate(String tripItemId, TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        LocalDate pastDate = tripItem.getVisitDate();
        LocalDate newDate = LocalDate.parse(visitDateUpdateMsg.visitDate());
        /*
        비즈니스 로직
         */

        TripItemMsg tripItemMsgToPastDate = new TripItemMsg(
            tripItem.getTourItem().getId(), pastDate.toString(), null
        );
        TripItemMsg tripItemMsgToNewDate = new TripItemMsg(
            tripItem.getTourItem().getId(), newDate.toString(), null
        );

        TripPathMsg tripPathMsgToPastDate = new TripPathMsg(
            tripItem.getTourItem().getId(), pastDate.toString(), null
        );
        TripPathMsg tripPathMsgToNewDate = new TripPathMsg(
            tripItem.getTourItem().getId(), newDate.toString(), null
        );

        kafkaProducer.send(TRIP_ITEM, tripItemMsgToPastDate);
        kafkaProducer.send(TRIP_ITEM, tripItemMsgToNewDate);
        kafkaProducer.send(PATH, tripPathMsgToPastDate);
        kafkaProducer.send(PATH, tripPathMsgToNewDate);
        // TODO : budget

    }
    @Transactional
    public void deleteTripItem(String tripItemId) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        LocalDate visitDate = tripItem.getVisitDate();
        /*
        비즈니스 로직
         */
        TripItemMsg tripItemMsg = new TripItemMsg(
            Long.parseLong(tripItemId), visitDate.toString(), null
        );
        TripPathMsg tripPathMsg = new TripPathMsg(
            Long.parseLong(tripItemId), visitDate.toString(), null
        );

        kafkaProducer.send(TRIP_ITEM, tripItemMsg);
        kafkaProducer.send(PATH, tripPathMsg);
        // TODO : budget

    }
    @Transactional
    public void updateTripItemTransportation(String tripItemId, TripItemTransportationUpdateMsg tripItemTransportationUpdateMsg) {

    }
}
