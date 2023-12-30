package org.tenten.tentenstomp.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.tour.entity.TourItem;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikedItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "likedItemId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tourItemId")
    private TourItem tourItem;
}