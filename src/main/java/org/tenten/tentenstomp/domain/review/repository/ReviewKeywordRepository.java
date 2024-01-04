package org.tenten.tentenstomp.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.review.entity.ReviewKeyword;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {
}
