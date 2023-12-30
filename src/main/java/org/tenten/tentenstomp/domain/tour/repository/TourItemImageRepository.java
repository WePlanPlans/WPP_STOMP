package org.tenten.tentenstomp.domain.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.tour.entity.TourItemImage;

public interface TourItemImageRepository extends JpaRepository<TourItemImage, Long> {
}
