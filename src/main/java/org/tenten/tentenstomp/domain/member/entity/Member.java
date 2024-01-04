package org.tenten.tentenstomp.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.comment.entity.Comment;
import org.tenten.tentenstomp.domain.review.entity.Review;
import org.tenten.tentenstomp.domain.trip.entity.Trip;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.entity.TripMember;
import org.tenten.tentenstomp.global.common.BaseTimeEntity;
import org.tenten.tentenstomp.global.common.enums.AgeType;
import org.tenten.tentenstomp.global.common.enums.GenderType;
import org.tenten.tentenstomp.global.common.enums.LoginType;
import org.tenten.tentenstomp.global.common.enums.UserAuthority;

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
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "memberId")
    private Long id;
    private String email;
    private String password;
    private String name; // 본명,
    private String nickname; // 닉네임
    private String profileImageUrl; // 프사 url
    @Convert(converter = Survey.SurveyConverter.class)
    @Column(columnDefinition = "JSON")
    private Survey survey;
    @Enumerated(STRING)
    private UserAuthority userAuthority;
    @Enumerated(STRING)
    private LoginType loginType;
    @Enumerated(STRING)
    private AgeType ageType;
    @Enumerated(STRING)
    private GenderType genderType;

    @OneToMany(mappedBy = "member", fetch = LAZY, cascade = REMOVE)
    private final List<TripMember> tripMembers = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = LAZY, cascade = REMOVE)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = LAZY, cascade = REMOVE)
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = LAZY, cascade = REMOVE)
    private final List<LikedItem> likedItems = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = LAZY, cascade = REMOVE)
    private final List<TripItem> tripItems = new ArrayList<>();
}