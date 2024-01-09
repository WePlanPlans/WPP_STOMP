package org.tenten.tentenstomp.domain.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;

import java.util.List;
import java.util.Map;

public interface TourItemRepository extends JpaRepository<TourItem, Long> {
    @Query("SELECT ti FROM TourItem ti WHERE ti.id in :tourItemIds ORDER BY ti.id ASC")
    public List<TourItem> findTourItemByTourItemIds(@Param("tourItems") List<Long> tourItemIds);
}
