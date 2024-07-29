package com.sparta.shop_sparta.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// abstract class로 인스턴스화 방지
// commit test
@MappedSuperclass
//@EntityListeners({AuditingEntityListener.class, BaseEntityListener.class})
@EntityListeners(AuditingEntityListener.class)
@Getter
//@SQLRestriction("is_deleted is true") //-> where deprecated 이후 사용할 어노테이션
public abstract class BaseEntity {
    @CreatedDate
    @Column( updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime lastModifyDate;

    private Boolean isDeleted = false;
    public void setDelete(Boolean isDelete) {
        this.isDeleted = isDelete;
    }
}
