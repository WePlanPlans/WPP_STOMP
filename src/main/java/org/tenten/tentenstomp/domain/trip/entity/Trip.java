package org.tenten.tentenstomp.domain.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.member.entity.Member;
import org.tenten.tentenstomp.global.common.BaseTimeEntity;
import org.tenten.tentenstomp.global.common.enums.TripStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = JOINED)
public class Trip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tripId")
    private Long id;
    private Long numberOfPeople; // 인원수
    private LocalDate startDate;
    private LocalDate endDate;
    private String area;
    private String subarea;
    @Enumerated(STRING)
    private TripStatus tripStatus;
    private Boolean isDeleted;
    private String tripName;
    private Long budget;

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripMember> tripMembers = new ArrayList<>();

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripItem> tripItems = new ArrayList<>();

    @OneToMany(mappedBy = "trip", fetch = LAZY, cascade = REMOVE)
    private final List<TripLikedItem> tripLikedItems = new ArrayList<>();

}