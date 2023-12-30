package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.trip.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
