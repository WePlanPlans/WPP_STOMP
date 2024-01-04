package org.tenten.tentenstomp.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
