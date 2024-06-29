package com.sparta.shop_sparta.domain.entity;


import com.sparta.shop_sparta.constant.ServerErrorMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PreRemove;

public class BaseEntityListener {
    
    // 수정 후 직접 commit 이 필요하다면 사용 -> persistence 관리 상태를 깨뜨릴 수 있을 것 같음... 확인 후 적용
    // private final EntityManager entityManager;

    @PreRemove
    public void preRemove(BaseEntity entity) {
        // entity.setDelete(true);
        // entityManager. -- > 커밋
        throw new IllegalStateException(ServerErrorMessage.PREVENT_ACTUAL_REMOVE.getMessage());
    }
}
