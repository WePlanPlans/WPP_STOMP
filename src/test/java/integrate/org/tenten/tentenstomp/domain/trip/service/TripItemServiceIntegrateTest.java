package org.tenten.tentenstomp.domain.trip.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;
import org.tenten.tentenstomp.domain.trip.dto.request.TripItemPriceUpdateMsg;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.repository.TripItemRepository;
import org.tenten.tentenstomp.domain.trip.repository.TripRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.tenten.tentenstomp.global.common.enums.Transportation.CAR;

@Slf4j
@SpringBootTest
class TripItemServiceIntegrateTest {
    @Autowired
    private TripItemService tripItemService;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TripItemRepository tripItemRepository;
    static List<Trip> trips;
    static List<TripItem> tripItems;
    @BeforeEach
    void beforeEach() {
        HashMap<String, Integer> tripPathPriceMap = new HashMap<>();
        LocalDate startDate =LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2L);
        HashMap<String, String> tripTransportationMap = new HashMap<>();
        tripPathPriceMap.put(startDate.toString(), 0);
        tripTransportationMap.put(startDate.toString(), CAR.getName());
        Trip trip = Trip.builder()
            .budget(20000L)
            .tripName("테스트용 여정")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2L))
            .numberOfPeople(2L)
            .tripItemPriceSum(0L)
            .transportationPriceSum(0)
            .isDeleted(false)
            .tripPathPriceMap(tripPathPriceMap)
            .tripTransportationMap(tripTransportationMap)
            .encryptedId("encryptedId")
            .build();
        trips = List.of(trip);
        tripRepository.saveAll(trips);

        TripItem tripItem1 = TripItem.builder()
            .tourItem(TourItem.builder().id(1L).build())
            .visitDate(LocalDate.now())
            .price(0L)
            .seqNum(1L)
            .trip(trip)
            .build();

        TripItem tripItem2 = TripItem.builder()
            .tourItem(TourItem.builder().id(7L).build())
            .visitDate(LocalDate.now())
            .price(0L)
            .seqNum(2L)
            .trip(trip)
            .build();

        TripItem tripItem3 = TripItem.builder()
            .tourItem(TourItem.builder().id(10L).build())
            .visitDate(LocalDate.now())
            .price(0L)
            .seqNum(3L)
            .trip(trip)
            .build();

        TripItem tripItem4 = TripItem.builder()
            .tourItem(TourItem.builder().id(12L).build())
            .visitDate(LocalDate.now())
            .price(0L)
            .seqNum(4L)
            .trip(trip)
            .build();


        tripItems = List.of(
            tripItem1, tripItem2, tripItem3, tripItem4
        );
        tripItemRepository.saveAll(tripItems);
    }

    @AfterEach
    void afterEach() {

        tripItemRepository.deleteAll(tripItems);
        tripRepository.deleteAll(trips);
    }

    @Test
    @DisplayName("redisson lock 테스트")
    public void testRedissonLock() throws Exception{
        // given
        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // when
        int[] array = IntStream.range(0, 30).toArray();
        for (int i : array) {
            executorService.submit(() -> {
                try {
                    TripItem tripItem = tripItems.get(i % tripItems.size());
                    Trip trip = tripItem.getTrip();
                    tripItemService.updateTripItemPrice(tripItem.getId().toString(), new TripItemPriceUpdateMsg(trip.getEncryptedId(), LocalDate.now().toString(), i + 100L));
                    tripItem.updatePrice(i + 100L);
                } finally {
                    latch.countDown();
                }
            });
        }
        // then
        latch.await();
        Optional<Trip> byEncryptedId = tripRepository.findByEncryptedId(trips.get(0).getEncryptedId());
        assert byEncryptedId.isPresent();
        Trip trip = byEncryptedId.get();
        log.info("tripPriceSum "+ String.valueOf(trip.getTripItemPriceSum()));
        List<TripItem> items = tripItemRepository.findTripItemByTripIdAndVisitDate(trips.get(0).getEncryptedId(), LocalDate.now());
        long priceSum = 0;
        for (TripItem tripItem : items) {
            priceSum += tripItem.getPrice();
        }
        log.info("priceSum "+priceSum);
        assert trip.getTripItemPriceSum().equals(priceSum);

    }
}