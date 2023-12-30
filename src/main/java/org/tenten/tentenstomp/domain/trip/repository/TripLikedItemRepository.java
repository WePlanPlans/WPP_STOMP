package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.trip.entity.TripLikedItem;

public interface TripLikedItemRepository extends JpaRepository<TripLikedItem, Long> {
}
