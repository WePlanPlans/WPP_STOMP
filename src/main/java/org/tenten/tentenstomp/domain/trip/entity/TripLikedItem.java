package org.tenten.tentenstomp.domain.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripLikedItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tripLikedItemId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tripId")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "tourItemId")
    private TourItem tourItem;

    @OneToMany(mappedBy = "tripLikedItem", fetch = LAZY, cascade = REMOVE)
    private final List<TripLikedItemPreference> tripLikedItemPreferences = new ArrayList<>();
}