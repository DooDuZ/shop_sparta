package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.domain.dto.constant.OrderResponseMessage;
import lombok.Getter;

@Getter
public class OrderResponseDTO {
    private Boolean result;
    private String message;

    public OrderResponseDTO(Boolean result, OrderResponseMessage message){
        this.result = result;
        this.message = message.getMessage();
    }
}
