package org.tenten.tentenstomp.domain.review.entity;

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
public class ReviewKeyword {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "reviewKeywordId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewId")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "keywordId")
    private Keyword keyword;
}