package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailRequestDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.OrderException;
import com.sparta.shop_sparta.repository.OrderDetailRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OrderDetailServiceImpl.class);

    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public List<OrderDetailDto> addOrder(OrderEntity orderEntity, List<OrderDetailRequestDto> orderDetailRequstDtoList) {
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

        // 저장 실패시 어짜피 transaction rollback 된다
        for (OrderDetailRequestDto orderDetailRequestDto : orderDetailRequstDtoList) {
            ProductEntity productEntity = productService.getProductEntity(orderDetailRequestDto.getProductId());

            // 주문 수량 0이하거나 재고 없다면
            if (orderDetailRequestDto.getAmount() <= 0 || productEntity.getAmount() < orderDetailRequestDto.getAmount()) {
                // 후에 메시지 케이스 별로 분리
                throw new OrderException(OrderResponseMessage.OUT_OF_STOCK.getMessage());
            }

            // Todo - 삭제 대상인지 확인 필요
            Long amount = productEntity.getAmount() - orderDetailRequestDto.getAmount();
            productService.setAmount(productEntity, amount);

            log.info(String.valueOf(amount));

            // 넣어줄 Entity가 많음... toEntity 말고 그냥 build로
            orderDetailEntities.add(
                    OrderDetailEntity.builder().orderEntity(orderEntity)
                            .productEntity(productEntity).amount(orderDetailRequestDto.getAmount()).build()
            );
        }

        orderDetailRepository.saveAll(orderDetailEntities);

        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            OrderDetailDto orderDetailDto = orderDetailEntity.toDto();
            orderDetailDto.setProductDto(productService.getProductDto(orderDetailEntity.getProductEntity()));

            orderDetailDtoList.add(orderDetailDto);
        }

        return orderDetailDtoList;
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
