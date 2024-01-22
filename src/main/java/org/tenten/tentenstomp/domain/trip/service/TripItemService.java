package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemDeleteMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripBudgetMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathMsg;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.repository.MessageProxyRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalDate.parse;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg.fromTripItemList;
import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;
import static org.tenten.tentenstomp.global.common.enums.Transportation.fromName;
import static org.tenten.tentenstomp.global.component.dto.request.TripPlace.fromTripItems;
import static org.tenten.tentenstomp.global.util.SequenceUtil.updateSeqNum;

@Service
@RequiredArgsConstructor
public class TripItemService {
    private final TripItemRepository tripItemRepository;
    private final TripRepository tripRepository;
    private final KafkaProducer kafkaProducer;
    private final PathComponent pathComponent;
    private final MessageProxyRepository messageProxyRepository;

    @Transactional
    public void updateTripItemPrice(String tripItemId, TripItemPriceUpdateMsg priceUpdateMsg) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findTripItemForUpdate(Long.parseLong(tripItemId));
        if (optionalTripItem.isEmpty()) {
            Trip trip = tripRepository.findByEncryptedId(priceUpdateMsg.tripId()).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정 X ", NOT_FOUND));
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripBudgetMsg(trip),
                messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), priceUpdateMsg.visitDate())
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Long oldPrice = tripItem.getPrice();
            Long newPrice = priceUpdateMsg.price();
            Trip trip = tripItem.getTrip();
            Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
            String transportation = tripTransportationMap.getOrDefault(priceUpdateMsg.visitDate(), CAR.getName());
            trip.updateTripItemPriceSum(oldPrice, newPrice);
            tripItem.updatePrice(newPrice);
            List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getEncryptedId(), parse(priceUpdateMsg.visitDate()));
            TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);
            TripItemMsg tripItemMsg = fromTripItemList(trip.getEncryptedId(), tripItem.getVisitDate().toString(), tripItems, tripItem.getId(), fromName(transportation), priceUpdateMsg);

            tripRepository.save(trip);

            kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg);
        }

    }

    @Transactional
    public void updateTripItemVisitDate(String tripItemId, TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findTripItemForUpdate(Long.parseLong(tripItemId));
        if (optionalTripItem.isEmpty()) {
            Trip trip = tripRepository.findByEncryptedId(visitDateUpdateMsg.tripId()).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 여정 X", NOT_FOUND));
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), visitDateUpdateMsg.oldVisitDate()),
                messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), visitDateUpdateMsg.newVisitDate()),
                messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), visitDateUpdateMsg.oldVisitDate()),
                messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), visitDateUpdateMsg.newVisitDate()),
                messageProxyRepository.getTripBudgetMsg(trip)
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Trip trip = tripItem.getTrip();
            Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
            LocalDate pastDate = tripItem.getVisitDate();
            String pastDateTransportation = tripTransportationMap.getOrDefault(pastDate.toString(), CAR.getName());
            LocalDate newDate = parse(visitDateUpdateMsg.newVisitDate());
            String newDateTransportation = tripTransportationMap.getOrDefault(newDate.toString(), CAR.getName());
            if (pastDate.equals(newDate)) {
                kafkaProducer.sendAndSaveToRedis(
                    messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), pastDate.toString()),
                    messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), pastDate.toString()),
                    messageProxyRepository.getTripBudgetMsg(trip)
                );
            } else {
                List<TripItem> pastDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getEncryptedId(), pastDate);
                List<TripItem> newDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getEncryptedId(), newDate);

                Long oldSeqNum = tripItem.getSeqNum();
                Long newSeqNum = (long) newDateTripItems.size() + 1;
                List<TripItem> newPastDateTripItems = new ArrayList<>();
                for (TripItem pastDateTripItem : pastDateTripItems) {
                    if (pastDateTripItem.getId().equals(tripItem.getId())) {
                        continue;
                    }
                    if (pastDateTripItem.getSeqNum() > oldSeqNum) {
                        pastDateTripItem.updateSeqNum(pastDateTripItem.getSeqNum() - 1);
                    }
                    newPastDateTripItems.add(pastDateTripItem);
                }

                tripItem.updateSeqNum(newSeqNum);
                tripItem.updateVisitDate(parse(visitDateUpdateMsg.newVisitDate()));
                newDateTripItems.add(tripItem);

                updateSeqNum(newPastDateTripItems);
                updateSeqNum(newDateTripItems);

                TripPathCalculationResult pastDateTripPath = pathComponent.getTripPath(fromTripItems(newPastDateTripItems), fromName(pastDateTransportation));
                TripPathCalculationResult newDateTripPath = pathComponent.getTripPath(fromTripItems(newDateTripItems), fromName(newDateTransportation));

                Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
                trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(pastDate.toString(), 0), pastDateTripPath.pathPriceSum());
                trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(newDate.toString(), 0), newDateTripPath.pathPriceSum());
                tripPathPriceMap.put(pastDate.toString(), pastDateTripPath.pathPriceSum());
                tripPathPriceMap.put(newDate.toString(), newDateTripPath.pathPriceSum());
                trip.updateTripPathPriceMap(tripPathPriceMap);
                tripRepository.save(trip);

                TripItemMsg pastDateTripItemMsg = fromTripItemList(trip.getEncryptedId(), pastDate.toString(), fromName(pastDateTransportation), newPastDateTripItems);
                TripItemMsg newDateTripItemMsg = fromTripItemList(trip.getEncryptedId(), newDate.toString(), fromName(newDateTransportation), newDateTripItems);
                TripPathMsg pastDateTripPathMsg = new TripPathMsg(trip.getEncryptedId(), pastDate.toString(), fromName(pastDateTransportation), pastDateTripPath.tripPathInfoMsgs());
                TripPathMsg newDateTripPathMsg = new TripPathMsg(trip.getEncryptedId(), newDate.toString(), fromName(newDateTransportation), newDateTripPath.tripPathInfoMsgs());
                TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);

                kafkaProducer.sendAndSaveToRedis(pastDateTripItemMsg, newDateTripItemMsg, pastDateTripPathMsg, newDateTripPathMsg, tripBudgetMsg);
            }

        }

    }

    @Transactional
    public void deleteTripItem(String tripItemId, TripItemDeleteMsg tripItemDeleteMsg) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findTripItemForDelete(Long.parseLong(tripItemId));
        if (optionalTripItem.isEmpty()) {
            Trip trip = tripRepository.findByEncryptedId(tripItemDeleteMsg.tripId()).orElseThrow(() -> new GlobalException("아이디로 존재하는 여정 X", NOT_FOUND));
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripItemMsg(trip.getEncryptedId(), tripItemDeleteMsg.visitDate()),
                messageProxyRepository.getTripPathMsg(trip.getEncryptedId(), tripItemDeleteMsg.visitDate()),
                messageProxyRepository.getTripBudgetMsg(trip)
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Trip trip = tripItem.getTrip();
            Map<String, String> tripTransportationMap = trip.getTripTransportationMap();
            LocalDate visitDate = tripItem.getVisitDate();
            String transportation = tripTransportationMap.getOrDefault(visitDate.toString(), CAR.getName());

            List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getEncryptedId(), visitDate);
            Long seqNum = tripItem.getSeqNum();
            List<TripItem> newTripItems = new ArrayList<>();
            for (TripItem newTripItem : tripItems) {
                if (newTripItem.getId().equals(tripItem.getId())) {
                    continue;
                }
                if (newTripItem.getSeqNum() > seqNum) {
                    newTripItem.updateSeqNum(newTripItem.getSeqNum() - 1);
                }
                newTripItems.add(newTripItem);
            }
            updateSeqNum(newTripItems);

            tripItemRepository.delete(tripItem);
            TripPathCalculationResult tripPath = pathComponent.getTripPath(fromTripItems(newTripItems), fromName(transportation));
            Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
            trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate.toString(), 0), tripPath.pathPriceSum());
            tripPathPriceMap.put(visitDate.toString(), tripPath.pathPriceSum());
            trip.updateTripTransportationMap(tripTransportationMap);
            trip.updateTripPathPriceMap(tripPathPriceMap);
            tripRepository.save(trip);

            TripItemMsg tripItemMsg = fromTripItemList(trip.getEncryptedId(), visitDate.toString(), fromName(transportation), newTripItems);
            TripPathMsg tripPathMsg = new TripPathMsg(trip.getEncryptedId(), visitDate.toString(), fromName(transportation), tripPath.tripPathInfoMsgs());
            TripBudgetMsg tripBudgetMsg = TripBudgetMsg.fromEntity(trip);

            kafkaProducer.sendAndSaveToRedis(tripItemMsg, tripPathMsg, tripBudgetMsg);
        }


    }

}
