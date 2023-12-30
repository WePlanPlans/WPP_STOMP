package org.tenten.tentenstomp.domain.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.tour.entity.TourItemDetail;

public interface TourItemDetailRepository extends JpaRepository<TourItemDetail, Long> {
}
