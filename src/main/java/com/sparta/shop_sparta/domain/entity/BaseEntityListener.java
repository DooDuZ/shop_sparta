package com.sparta.shop_sparta.domain.entity;


import com.sparta.shop_sparta.constant.ServerErrorMessage;
import jakarta.persistence.PreRemove;

public class BaseEntityListener {

    @PreRemove
    public void preRemove(BaseEntity entity) {
        entity.setDelete(true);
        throw new IllegalStateException(ServerErrorMessage.PREVENT_ACTUAL_REMOVE.getMessage());
    }
}
