package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;

public interface TripItemRepository extends JpaRepository<TripItem, Long> {
}
