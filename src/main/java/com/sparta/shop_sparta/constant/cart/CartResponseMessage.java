package com.sparta.shop_sparta.constant.cart;

public enum CartResponseMessage {

    INVALID_CART_ID("장바구니 정보를 찾을 수 없습니다.")
    ;

    private final String message;

    CartResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
