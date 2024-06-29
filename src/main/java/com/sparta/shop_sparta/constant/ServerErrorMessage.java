package com.sparta.shop_sparta.constant;

public enum ServerErrorMessage {
    PREVENT_ACTUAL_REMOVE("Soft Delete 적용 - 물리적 삭제 시도 발생"),
    ;

    private final String message;

    ServerErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
