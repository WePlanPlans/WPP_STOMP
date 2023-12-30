package org.tenten.tentenstomp.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
