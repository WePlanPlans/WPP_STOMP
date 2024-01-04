package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.trip.entity.TripLikedItemPreference;

public interface TripLikedItemPreferenceRepository extends JpaRepository<TripLikedItemPreference, Long> {
}
