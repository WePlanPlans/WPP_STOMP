package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface TripItemRepository extends JpaRepository<TripItem, Long> {
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT ti FROM TripItem ti JOIN FETCH ti.tourItem WHERE ti.trip.encryptedId = :tripId AND ti.visitDate = :visitDate ORDER BY ti.seqNum ASC")
    List<TripItem> findTripItemByTripIdAndVisitDate(@Param("tripId") String tripId, @Param("visitDate") LocalDate visitDate);
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT ti FROM TripItem ti JOIN FETCH ti.trip WHERE ti.id = :tripItemId")
    Optional<TripItem> findTripItemForUpdate(@Param("tripItemId") Long tripItemId);
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT ti FROM TripItem ti JOIN FETCH ti.trip WHERE ti.id = :tripItemId")
    Optional<TripItem> findTripItemForDelete(@Param("tripItemId") Long tripItemId);
}
