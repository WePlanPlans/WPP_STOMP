package org.tenten.tentenstomp.domain.comment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tenten.tentenstomp.domain.member.entity.Member;
import org.tenten.tentenstomp.domain.review.entity.Review;
import org.tenten.tentenstomp.global.common.BaseTimeEntity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = JOINED)
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "commentId")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member creator;

    @ManyToOne
    @JoinColumn(name = "reviewId")
    private Review review;


    public Comment(String content , Review review ){
        this.content = content;
        this.review = review;
        // this.creator = creator;
    }

    public void UpdateComment(String content){
        this.content = content;
    }

    // 리뷰와 댓글 양방향 설정 만약 리뷰에서 설정되어있으면 제거해도됨
    public void addReview(Review review){
        this.review = review;

        if(!review.getComments().contains(this)){
            review.getComments().add(this);
        }
    }
    public void removeReview(){
        if(this.review != null){
            review.getComments().remove(this);
            this.review = null;
        }
    }

    public void addCreator(Member creator){
        this.creator = creator;

        if(!creator.getComments().contains(this)){
            creator.getComments().add(this);
        }
    }
    public void removeCreator(){
        if(this.creator != null){
            creator.getComments().remove(this);
            this.creator = null;
        }
    }
}