package org.tenten.tentenstomp.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tenten.tentenstomp.domain.member.entity.Member;
import org.tenten.tentenstomp.domain.trip.dto.response.TripMemberInfoMsg.TripMemberInfo;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT NEW org.tenten.tentenstomp.domain.trip.dto.response.TripMemberInfoMsg.TripMemberInfo(m.id, m.nickname, m.profileImageUrl) " +
        "FROM TripMember tm LEFT OUTER JOIN Member m ON tm.member.id = m.id WHERE tm.trip.id = :tripId")
    List<TripMemberInfo> findTripMemberInfoByTripId(@Param("tripId") Long tripId);
    @Query("SELECT NEW org.tenten.tentenstomp.domain.trip.dto.response.TripMemberInfoMsg.TripMemberInfo(m.id, m.nickname, m.profileImageUrl) FROM Member m WHERE m.id = :memberId")
    Optional<TripMemberInfo> findTripMemberInfoByMemberId(@Param("memberId") Long memberId);
}
