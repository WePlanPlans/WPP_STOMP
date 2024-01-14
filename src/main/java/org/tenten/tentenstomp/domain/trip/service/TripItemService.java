package org.tenten.tentenstomp.domain.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemTransportationUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemVisitDateUpdateMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripBudgetMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemMsg;
import org.tenten.tentenstomp.domain.trip.dto.response.TripPathMsg;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;
import org.tenten.tentenstomp.global.component.PathComponent;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;
import org.tenten.tentenstomp.global.component.dto.response.TripPathCalculationResult;
import org.tenten.tentenstomp.global.exception.GlobalException;
import org.tenten.tentenstomp.global.messaging.kafka.producer.KafkaProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TripItemService {
    private final TripItemRepository tripItemRepository;
    private final TripRepository tripRepository;
    private final KafkaProducer kafkaProducer;
    private final PathComponent pathComponent;
    @Transactional
    public void updateTripItemPrice(String tripItemId, TripItemPriceUpdateMsg priceUpdateMsg) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        Long oldPrice = tripItem.getPrice();
        Long newPrice = priceUpdateMsg.price();
        Trip trip = tripItem.getTrip();
        trip.updateTripItemPriceSum(oldPrice, newPrice);
        tripItem.updatePrice(newPrice);
        List<TripItem> tripItems = trip.getTripItems();
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());
        TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), tripItem.getVisitDate().toString(), tripItems, tripItem.getId(), priceUpdateMsg);
        kafkaProducer.sendAndSaveToRedis(tripBudgetMsg, tripItemMsg);
    }
    @Transactional
    public void updateTripItemVisitDate(String tripItemId, TripItemVisitDateUpdateMsg visitDateUpdateMsg) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        Trip trip = tripRepository.getReferenceById(tripItem.getTrip().getId());
        LocalDate pastDate = tripItem.getVisitDate();
        LocalDate newDate = LocalDate.parse(visitDateUpdateMsg.visitDate());

        List<TripItem> pastDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), pastDate);
        List<TripItem> newDateTripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), newDate);

        Long oldSeqNum = tripItem.getSeqNum();
        Long newSeqNum = (long) newDateTripItems.size()+1;
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
        tripItem.updateVisitDate(LocalDate.parse(visitDateUpdateMsg.visitDate()));
        newDateTripItems.add(tripItem);

        TripPathCalculationResult pastDateTripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newPastDateTripItems));
        TripPathCalculationResult newDateTripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newDateTripItems));

        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(pastDate.toString(), 0), pastDateTripPath.pathPriceSum());
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(newDate.toString(), 0), newDateTripPath.pathPriceSum());
        tripPathPriceMap.put(pastDate.toString(), pastDateTripPath.pathPriceSum());
        tripPathPriceMap.put(newDate.toString(), newDateTripPath.pathPriceSum());
        tripRepository.save(trip);

        TripItemMsg pastDateTripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), pastDate.toString(), newPastDateTripItems);
        TripItemMsg newDateTripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), newDate.toString(), newDateTripItems);
        TripPathMsg pastDateTripPathMsg = new TripPathMsg(trip.getId(), pastDate.toString(), pastDateTripPath.tripPathInfoMsgs());
        TripPathMsg newDateTripPathMsg = new TripPathMsg(trip.getId(), newDate.toString(), newDateTripPath.tripPathInfoMsgs());
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());

        kafkaProducer.sendAndSaveToRedis(pastDateTripItemMsg, newDateTripItemMsg, pastDateTripPathMsg, newDateTripPathMsg, tripBudgetMsg);

    }
    @Transactional
    public void deleteTripItem(String tripItemId) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        Trip trip = tripRepository.getReferenceById(tripItem.getTrip().getId());

        LocalDate visitDate = tripItem.getVisitDate();

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
        TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(newTripItems));
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate.toString(), 0), tripPath.pathPriceSum());
        tripPathPriceMap.put(visitDate.toString(), tripPath.pathPriceSum());
        tripRepository.save(trip);

        TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), visitDate.toString(), newTripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate.toString(), tripPath.tripPathInfoMsgs());
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());

        kafkaProducer.sendAndSaveToRedis(tripItemMsg, tripPathMsg, tripBudgetMsg);

    }
    @Transactional
    public void updateTripItemTransportation(String tripItemId, TripItemTransportationUpdateMsg tripItemTransportationUpdateMsg) {
        TripItem tripItem = tripItemRepository.findById(Long.parseLong(tripItemId)).orElseThrow(() -> new GlobalException("해당 아이디로 존재하는 tripItem이 없다 " + tripItemId, NOT_FOUND));
        Trip trip = tripRepository.getReferenceById(tripItem.getTrip().getId());

        LocalDate visitDate = tripItem.getVisitDate();
        List<TripItem> tripItems = tripItemRepository.findTripItemByTripIdAndVisitDate(tripItem.getTrip().getId(), visitDate);
        for (TripItem newTripItem : tripItems) {
            if (newTripItem.getId().equals(tripItem.getId())) {
                newTripItem.updateTransportation(tripItemTransportationUpdateMsg.transportation());
            }
        }

        tripItem.updateTransportation(tripItemTransportationUpdateMsg.transportation());
        tripItemRepository.save(tripItem);

        TripPathCalculationResult tripPath = pathComponent.getTripPath(TripPlace.fromTripItems(tripItems));
        Map<String, Integer> tripPathPriceMap = trip.getTripPathPriceMap();
        trip.updateTransportationPriceSum(tripPathPriceMap.getOrDefault(visitDate.toString(), 0), tripPath.pathPriceSum());
        tripPathPriceMap.put(visitDate.toString(), tripPath.pathPriceSum());
        tripRepository.save(trip);

        TripItemMsg tripItemMsg = TripItemMsg.fromTripItemList(trip.getId(), visitDate.toString(), tripItems);
        TripPathMsg tripPathMsg = new TripPathMsg(trip.getId(), visitDate.toString(), tripPath.tripPathInfoMsgs());
        TripBudgetMsg tripBudgetMsg = new TripBudgetMsg(trip.getId(), trip.getBudget(), trip.getTripItemPriceSum() + trip.getTransportationPriceSum());

        kafkaProducer.sendAndSaveToRedis(tripItemMsg, tripPathMsg, tripBudgetMsg);
    }


}
