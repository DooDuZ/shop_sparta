package com.sparta.shop_sparta.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// abstract class로 인스턴스화 방지
// commit test
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
// @SQLRestriction("deleted_at is NULL") -> where deprecated 이후 사용할 어노테이션
public abstract class BaseEntity {
    @CreatedDate
    @Column( updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime lastModifyDate;

    @Getter
    private Boolean isDelete = false;
    public void setDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}
