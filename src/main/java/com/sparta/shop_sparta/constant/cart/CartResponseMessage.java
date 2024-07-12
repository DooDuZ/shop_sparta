package com.sparta.shop_sparta.constant.cart;

import org.springframework.http.HttpStatus;

public enum CartResponseMessage {

    INVALID_CART_ID("장바구니 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_ON_SALE("판매 중인 상품이 아닙니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    CartResponseMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
