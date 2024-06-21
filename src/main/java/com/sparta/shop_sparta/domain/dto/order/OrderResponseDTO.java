package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.domain.dto.ResponseDTO;
import com.sparta.shop_sparta.domain.dto.constant.OrderResponseMessage;
import lombok.Getter;

@Getter
public class OrderResponseDTO extends ResponseDTO {
    OrderDTO orderDTO;
    public OrderResponseDTO(Boolean result, OrderResponseMessage message, OrderDTO orderDTO){
        super(result, message.getMessage());
        this.orderDTO = orderDTO;
    }
}
