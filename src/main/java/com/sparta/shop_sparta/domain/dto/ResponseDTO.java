package com.sparta.shop_sparta.domain.dto;

public abstract class ResponseDTO {
    final Boolean result;
    final String message;

    public ResponseDTO(Boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
