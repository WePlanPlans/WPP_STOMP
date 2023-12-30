package org.tenten.tentenstomp.domain.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.member.entity.Member;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;
import org.tenten.tentenstomp.global.common.BaseTimeEntity;
import org.tenten.tentenstomp.global.common.enums.Transportation;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = JOINED)
public class TripItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tripItemId")
    private Long id;
    @Enumerated(STRING)
    private Transportation transportation;
    private Long seqNum; // 방문 순서
    private LocalDate visitDate;
    private Long price; // 예상 비용

    @ManyToOne
    @JoinColumn(name = "tripId")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member creator;

    @ManyToOne
    @JoinColumn(name = "tourItemId")
    private TourItem tourItem;
}