package org.tenten.tentenstomp.domain.tour.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.review.entity.Review;
import org.tenten.tentenstomp.domain.trip.entity.TripItem;
import org.tenten.tentenstomp.domain.trip.entity.TripLikedItem;

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
public class TourItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tourItemId")
    private Long id;
    private Long contentId; // open api 컨텐츠 아이디
    private Long contentTypeId; // open api 컨텐츠 타입 아이디 (카테고리 타입)
    private Long areaCode; // 지역 코드
    private Long subAreaCode; // 시군구 코드
    private String address;
    private String detailedAddress;
    private String originalThumbnailUrl;
    private String smallThumbnailUrl;
    private String title; // 여행지 이름
    private String zipcode; // 우편번호
    private String tel; // 전화번호
    private String longitude; // x좌표
    private String latitude; // y좌표

    @OneToMany(mappedBy = "tourItem", fetch = LAZY, cascade = REMOVE)
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "tourItem", fetch = LAZY, cascade = REMOVE)
    private final List<TripItem> tripItems = new ArrayList<>();

    @OneToOne(mappedBy = "tourItem", cascade = REMOVE)
    private TourItemDetail tourItemDetail;

    @OneToOne(mappedBy = "tourItem", cascade = REMOVE)
    private TourItemImage tourItemImage;

    @OneToMany(mappedBy = "tourItem", fetch = LAZY, cascade = REMOVE)
    private final List<TripLikedItem> tripLikedItems = new ArrayList<>();
}