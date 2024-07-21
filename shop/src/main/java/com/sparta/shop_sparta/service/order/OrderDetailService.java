package com.sparta.shop_sparta.service.order;

import com.sparta.common.constant.order.OrderResponseMessage;
import com.sparta.common.exception.OrderException;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailRequestDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.repository.OrderDetailRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import com.sparta.shop_sparta.service.product.StockService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;
    private final StockService stockService;

    @Transactional
    public List<OrderDetailEntity> addOrder(OrderEntity orderEntity, List<OrderDetailRequestDto> orderDetailRequstDtoList) {
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();

        // 저장 실패시 어짜피 transaction rollback 된다
        for (OrderDetailRequestDto orderDetailRequestDto : orderDetailRequstDtoList) {
            Long productId = orderDetailRequestDto.getProductId();
            if (!stockService.isCached(productId)){
                StockEntity stockEntity = stockService.getStockByProductId(orderDetailRequestDto.getProductId());
                if (stockEntity.getAmount() > 0){
                    stockService.redisCache(productId);
                }else{
                    throw new OrderException(OrderResponseMessage.OUT_OF_STOCK);
                }
            }

            Long stock = stockService.getStockInRedis(productId);
            Long amount = orderDetailRequestDto.getAmount();

            validateOrderRequest(stock, amount);

            // redis 재고 update
            stockService.updateStockInRedis(productId, amount);
            // 비동기 sql query 실행
            StockEntity stockEntity = stockService.getStockByProductId(productId);
            stockService.updateStockAfterOrder(stockEntity.getStockId(), amount);

            // 넣어줄 Entity가 많음... toEntity 말고 그냥 build로
            orderDetailEntities.add(
                    OrderDetailEntity.builder().orderEntity(orderEntity)
                            .productEntity(stockEntity.getProductEntity()).amount(orderDetailRequestDto.getAmount()).build()
            );
        }

        return orderDetailEntities;
    }

    @Async
    public void orderDetailSaveAll(List<OrderDetailEntity> orderDetailEntities){
        orderDetailRepository.saveAll(orderDetailEntities);
    }

    public List<OrderDetailEntity> getOrderDetailsByOrderEntities(List<OrderEntity> orderEntities) {
        return orderDetailRepository.findAllByOrderEntityIn(orderEntities);
    }

    public void rollbackOrder(List<OrderDetailEntity> orderDetailEntityList) {
        for (OrderDetailEntity orderDetailEntity : orderDetailEntityList) {
            stockService.repairStock(orderDetailEntity);
            stockService.updateStockInRedis(orderDetailEntity.getProductEntity().getProductId(), -orderDetailEntity.getAmount());
        }
    }


    private void validateOrderRequest(Long stock, Long amount){
        if(amount <= 0){
            throw new OrderException(OrderResponseMessage.INVALID_AMOUNT);
        }

        if (stock < amount){
            throw new OrderException(OrderResponseMessage.OUT_OF_STOCK);
        }
    }

    private List<OrderDetailDto> getOrderDetailDtoList(List<OrderDetailEntity> orderDetailEntities) {
        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            OrderDetailDto orderDetailDto = orderDetailEntity.toDto();
            orderDetailDto.setProductDto(productService.getProductDto(orderDetailEntity.getProductEntity()));

            orderDetailDtoList.add(orderDetailDto);
        }
        return orderDetailDtoList;
    }

    public List<OrderDetailDto> getOrderedProduct(OrderEntity orderEntity) {
        return orderDetailRepository.findByOrderEntity(orderEntity).stream()
                .map(OrderDetailEntity::toDto)
                .toList();
    }

    @Transactional
    public void cancelOrder(OrderEntity orderEntity) {
        List<OrderDetailEntity> orderDetailEntities = orderDetailRepository.findByOrderEntity(orderEntity);
        // 재고 반환 처리
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            StockEntity stockEntity = stockService.getStockEntity(orderDetailEntity.getProductEntity());
            stockEntity.setAmount(stockEntity.getAmount() + orderDetailEntity.getAmount());
        }
    }
}