package com.sparta.shop_sparta.domain.entity.item.constant;

public enum ItemStatus {
    NOT_PUBLISHED,  // 공개 전
    WAITING,        // 공개, 판매 대기
    ON_SALE,        // 판매 중
    ENDED_SALE,     // 판매 종료
    SUSPENDED_SALE  // 판매 중단
}
