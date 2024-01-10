package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.trip.entity.Trip;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t JOIN FETCH t.tripItems WHERE t.id = :tripId")
    Optional<Trip> findTripByTripId(@Param("tripId") Long tripId);
}
