package com.sparta.shop_sparta.domain.dto.order;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderDTO {
    private Long orderId;
    private String order_addr;
    private Long memberId;
    private String orderStatus;
    private List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();


}
