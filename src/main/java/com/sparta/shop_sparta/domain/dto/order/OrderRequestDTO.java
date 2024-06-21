package com.sparta.shop_sparta.domain.dto.order;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestDTO {
    private String order_addr;
    private Long memberId;
    private List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
}
