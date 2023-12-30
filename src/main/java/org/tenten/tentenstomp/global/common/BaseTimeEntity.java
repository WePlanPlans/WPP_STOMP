package org.tenten.tentenstomp.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false, name = "createdTime")
    private LocalDateTime createdTime;
    @LastModifiedDate
    @Column(name = "modifiedTime")
    private LocalDateTime modifiedTime;
}