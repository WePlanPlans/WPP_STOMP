package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.trip.entity.Trip;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByEncryptedId(String encryptedId);

    @Query("SELECT t FROM Trip t LEFT OUTER JOIN FETCH t.tripItems WHERE t.encryptedId = :tripId")
    Optional<Trip> findTripForUpdate(@Param("tripId") String tripId);
}
