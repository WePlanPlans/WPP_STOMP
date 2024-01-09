package org.tenten.tentenstomp.domain.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripLikedItemPreference {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tripLikedItemPreferenceId")
    private Long id;

    private Boolean prefer;
    private Boolean notPrefer;

    @ManyToOne
    @JoinColumn(name = "tripMemberId")
    private TripMember tripMember;

    @ManyToOne
    @JoinColumn(name = "tripLikedItemId")
    private TripLikedItem tripLikedItem;
}