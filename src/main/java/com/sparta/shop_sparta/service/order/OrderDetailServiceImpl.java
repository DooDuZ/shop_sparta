package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.OrderDetailRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public Long addOrder(OrderEntity orderEntity, List<OrderDetailDto> orderDetailDtoList) {
        Long totalPrice = 0L;

        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

        // 저장 실패시 어짜피 transaction rollback 된다
        for (OrderDetailDto orderDetailDto : orderDetailDtoList) {
            ProductEntity productEntity = productService.getProductEntity(orderDetailDto.getProductResponseDto().getProductId());

            // 주문 수량 0이하거나 재고 없다면
            if (orderDetailDto.getAmount() <= 0 || productEntity.getAmount() < orderDetailDto.getAmount()) {
                // 후에 메시지 케이스 별로 분리
                throw new ProductException(OrderResponseMessage.OUT_OF_STOCK.getMessage());
            }

            totalPrice += productEntity.getPrice() * orderDetailDto.getAmount();
             productEntity.setAmount(productEntity.getAmount() - orderDetailDto.getAmount());

            // 넣어줄 Entity가 많음... toEntity 말고 그냥 build로
            orderDetailEntities.add(
                    OrderDetailEntity.builder().orderEntity(orderEntity)
                            .productEntity(productEntity).amount(orderDetailDto.getAmount()).build()
            );
        }

        orderDetailRepository.saveAll(orderDetailEntities);

        return totalPrice;
    }

    @Override
    public List<OrderDetailDto> getOrderedProduct(OrderEntity orderEntity) {
        return orderDetailRepository.findByOrderEntity(orderEntity).stream()
                .map(OrderDetailEntity::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(OrderEntity orderEntity) {
        List<OrderDetailEntity> orderDetailEntities = orderDetailRepository.findByOrderEntity(orderEntity);
        // 재고 반환 처리
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            ProductEntity productEntity = orderDetailEntity.getProductEntity();
            productEntity.setAmount(productEntity.getAmount() + orderDetailEntity.getAmount());
        }
    }
}
