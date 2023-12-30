package org.tenten.tentenstomp.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.member.entity.LikedItem;

public interface LikedItemRepository extends JpaRepository<LikedItem, Long> {
}
