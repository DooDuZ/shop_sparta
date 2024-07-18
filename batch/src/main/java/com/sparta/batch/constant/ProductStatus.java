package com.sparta.batch.constant;

public enum ProductStatus {
    NOT_PUBLISHED(1),   // 공개 전
    WAITING(2),         // 공개, 판매 대기
    ON_SALE(3),         // 판매 중
    ENDED_SALE(4),      // 판매 종료
    SUSPENDED_SALE(5),  // 판매 중단
    ;


    private final int value;

    ProductStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
