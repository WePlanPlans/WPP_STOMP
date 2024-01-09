package org.tenten.tentenstomp.domain.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;

public interface TourItemRepository extends JpaRepository<TourItem, Long> {
}
