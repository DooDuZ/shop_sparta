package com.sparta.shop_sparta.exception;

import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

    private final OrderResponseMessage error;

    public OrderException(OrderResponseMessage orderResponseMessage) {
        super(orderResponseMessage.getMessage());
        this.error = orderResponseMessage;
    }
    public OrderException(OrderResponseMessage orderResponseMessage, Throwable cause) {
        super(orderResponseMessage.getMessage(), cause);
        this.error = orderResponseMessage;
    }
}
