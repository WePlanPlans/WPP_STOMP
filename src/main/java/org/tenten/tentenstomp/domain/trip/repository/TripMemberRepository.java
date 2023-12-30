package org.tenten.tentenstomp.domain.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.trip.entity.TripMember;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
}
