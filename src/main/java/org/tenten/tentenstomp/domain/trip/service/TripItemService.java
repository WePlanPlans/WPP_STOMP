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
import org.tenten.tentenstomp.global.common.enums.Transportation;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;

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
            Trip trip = tripRepository.getReferenceById(priceUpdateMsg.tripId());
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripBudgetMsg(trip),
                messageProxyRepository.getTripItemMsg(trip.getId(), priceUpdateMsg.visitDate())
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Long oldPrice = tripItem.getPrice();
            Long newPrice = priceUpdateMsg.price();
            Trip trip = tripItem.getTrip();
            Transportation transportation = trip.getTripTransportationMap().getOrDefault(priceUpdateMsg.visitDate(), CAR);
            trip.updateTripItemPriceSum(oldPrice, newPrice);
            tripItem.updatePrice(newPrice);
            List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), LocalDate.parse(priceUpdateMsg.visitDate()));
            TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
            TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), tripItem.getVisitDate().toString(), tripItems, tripItem.getId(), transportation, priceUpdateMsg);
            kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg);
        }

    }

    @Transactional
    public void updateTripItemVisitDate(String tripItemId, TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findTripItemForUpdate(Long.parseLong(tripItemId));
        if (optionalTripItem.isEmpty()) {
            Trip trip = tripRepository.getReferenceById(visitDateUpdateMsg.tripId());
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripItemMsg(trip.getId(), visitDateUpdateMsg.oldVisitDate()),
                messageProxyRepository.getTripItemMsg(trip.getId(), visitDateUpdateMsg.newVisitDate()),
                messageProxyRepository.getTripPathMsg(trip.getId(), visitDateUpdateMsg.oldVisitDate()),
                messageProxyRepository.getTripPathMsg(trip.getId(), visitDateUpdateMsg.newVisitDate()),
                messageProxyRepository.getTripBudgetMsg(trip)
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Trip trip = tripRepository.getReferenceById(tripItem.getTrip().getId());
            Map<String, Transportation> tripTransportationMap = trip.getTripTransportationMap();
            LocalDate pastDate = tripItem.getVisitDate();
            Transportation pastDateTransportation = tripTransportationMap.getOrDefault(pastDate.toString(), CAR);
            LocalDate newDate = LocalDate.parse(visitDateUpdateMsg.newVisitDate());
            Transportation newDateTransportation = tripTransportationMap.getOrDefault(newDate.toString(), CAR);

            List<TripItem> pastDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), pastDate);
            List<TripItem> newDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), newDate);

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
            tripItem.updateVisitDate(LocalDate.parse(visitDateUpdateMsg.newVisitDate()));
            newDateTripItems.add(tripItem);

            TripPathCalculationResult pastDateTripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newPastDateTripItems), pastDateTransportation);
            TripPathCalculationResult newDateTripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newDateTripItems), newDateTransportation);

            Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
            trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(pastDate.toString(), 0), pastDateTripPath.pathPriceSum());
            trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(newDate.toString(), 0), newDateTripPath.pathPriceSum());
            tripPathPriceMap.put(pastDate.toString(), pastDateTripPath.pathPriceSum());
            tripPathPriceMap.put(newDate.toString(), newDateTripPath.pathPriceSum());
            tripRepository.save(trip);

            TripItemMsg pastDateTripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), pastDate.toString(), pastDateTransportation, newPastDateTripItems);
            TripItemMsg newDateTripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), newDate.toString(), newDateTransportation, newDateTripItems);
            TripPathMsg pastDateTripPathMsg = new TripPathMsg(trip.getId(), pastDate.toString(), pastDateTransportation, pastDateTripPath.tripPathInfoMsgs());
            TripPathMsg newDateTripPathMsg = new TripPathMsg(trip.getId(), newDate.toString(), newDateTransportation, newDateTripPath.tripPathInfoMsgs());
            TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());

            kafkaProducer.sendAndSaveToRedis(pastDateTripItemMsg, newDateTripItemMsg, pastDateTripPathMsg, newDateTripPathMsg, tripBudgetMsg);
        }

    }

    @Transactional
    public void deleteTripItem(String tripItemId, TripItemDeleteMsg tripItemDeleteMsg) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findTripItemForUpdate(Long.parseLong(tripItemId));
        if (optionalTripItem.isEmpty()) {
            Trip trip = tripRepository.getReferenceById(tripItemDeleteMsg.tripId());
            kafkaProducer.sendWithOutCaching(
                messageProxyRepository.getTripItemMsg(trip.getId(), tripItemDeleteMsg.visitDate()),
                messageProxyRepository.getTripPathMsg(trip.getId(), tripItemDeleteMsg.visitDate()),
                messageProxyRepository.getTripBudgetMsg(trip)
            );
        } else {
            TripItem tripItem = optionalTripItem.get();
            Trip trip = tripRepository.getReferenceById(tripItem.getTrip().getId());
            Map<String, Transportation> tripTransportationMap = trip.getTripTransportationMap();

            LocalDate visitDate = tripItem.getVisitDate();
            Transportation transportation = tripTransportationMap.getOrDefault(visitDate.toString(), CAR);

            List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), visitDate);
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

            tripItemRepository.delete(tripItem);
            TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newTripItems), transportation);
            Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
            trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate.toString(), 0), tripPath.pathPriceSum());
            tripPathPriceMap.put(visitDate.toString(), tripPath.pathPriceSum());
            tripRepository.save(trip);

            TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), visitDate.toString(),  transportation, newTripItems);
            TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate.toString(), transportation, tripPath.tripPathInfoMsgs());
            TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());

            kafkaProducer.sendAndSaveToRedis(tripItemMsg, tripPathMsg, tripBudgetMsg);
        }


    }

}
