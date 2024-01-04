package org.tenten.tentenstomp.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.review.entity.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
