package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.trip.dto.response.TripItemInfo;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.global.component.dto.request.TripPlace;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface TripItemRepository extends JpaRepository<TripItem, Long> {
    @Query("SELECT NEW org.tenten.tentenstomp.domain.trip.dto.response.TripItemInfo(" +
        "ti.id, t.id, t.title, t.originalThumbnailUrl, t.contentTypeId, ti.transportation, ti.seqNum, ti.visitDate, ti.price" +
        ") FROM TripItem ti LEFT OUTER JOIN TourItem t ON ti.tourItem.id = t.id WHERE ti.trip.id = :tripId AND ti.visitDate = :visitDate ORDER BY ti.seqNum ASC")
    List<TripItemInfo> getTripItemInfoByTripIdAndVisitDate(@Param("tripId") Long tripId, @Param("visitDate") LocalDate visitDate);

    @Query("SELECT ti FROM TripItem ti JOIN FETCH ti.tourItem WHERE ti.trip.id = :tripId AND ti.visitDate = :visitDate ORDER BY ti.seqNum ASC")
    List<TripItem> findTripItemByTripIdAndVisitDate(@Param("tripId") Long tripId, @Param("visitDate") LocalDate visitDate);
    @Query("SELECT NEW org.tenten.tentenstomp.global.component.dto.request.TripPlace(" +
        "ti.id, ti.seqNum, ti.transportation, t.longitude, t.latitude, ti.price" +
        ") FROM TripItem ti LEFT OUTER JOIN TourItem t ON ti.tourItem.id = t.id WHERE ti.trip.id = :tripId AND ti.visitDate = :visitDate ORDER BY ti.seqNum ASC")
    List<TripPlace> findTripPlaceByTripIdAndVisitDate(@Param("tripId") Long tripId, @Param("visitDate") LocalDate visitDate);
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT ti FROM TripItem ti WHERE ti.id = :tripId")
    Optional<TripItem> findTripItemForUpdate(@Param("tripId") Long tripId);
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT ti FROM TripItem ti WHERE ti.id = :tripId")
    Optional<TripItem> findTripItemForDelete(@Param("tripId") Long tripId);
}
