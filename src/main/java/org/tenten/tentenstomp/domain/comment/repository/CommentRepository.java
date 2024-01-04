package org.tenten.tentenstomp.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tenten.tentenstomp.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
