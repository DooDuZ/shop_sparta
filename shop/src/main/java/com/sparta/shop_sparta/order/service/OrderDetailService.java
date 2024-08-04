package com.sparta.shop_sparta.order.service;

import com.sparta.common.constant.order.OrderResponseMessage;
import com.sparta.common.exception.OrderException;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailDto;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.order.repository.OrderDetailRepository;
import com.sparta.shop_sparta.product.service.CustomerProductService;
import com.sparta.shop_sparta.product.service.StockService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailService {
    private final CustomerProductService productService;
    private final OrderDetailRepository orderDetailRepository;
    private final StockService stockService;

    @Transactional
    public List<OrderDetailDto> addOrder(OrderEntity orderEntity, List<OrderDetailRequestDto> orderDetailRequstDtoList) {
        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

        for (OrderDetailRequestDto orderDetailRequestDto : orderDetailRequstDtoList) {
            Long productId = orderDetailRequestDto.getProductId();

            // 재고 cache miss 시 캐싱
            checkCache(productId);

            Long stock = stockService.getStockInRedis(productId);
            Long amount = orderDetailRequestDto.getAmount();

            validateOrderRequest(stock, amount);

            // redis 재고 update
            stockService.updateStockInRedis(productId, amount);
            // 비동기 sql query 실행
            stockService.updateStockAfterOrder(productId, amount);

            // 상품 가격 정보 불러오기
            orderDetailDtoList.add(
                    OrderDetailDto.builder().orderId(orderEntity.getOrderId())
                            .productDto(
                                    ProductDto.builder()
                                            .productId(productId)
                                            .build()
                            )
                            .amount(orderDetailRequestDto.getAmount())
                            .build()
            );
        }

        return orderDetailDtoList;
    }

    private void checkCache(Long productId) {
        if (!stockService.isCached(productId)){
            StockEntity stockEntity = stockService.getStockByProductId(productId);
            if (stockEntity.getAmount() > 0){
                stockService.redisCache(productId);
            }else{
                throw new OrderException(OrderResponseMessage.OUT_OF_STOCK);
            }
        }
    }

    //@Async
    public void orderDetailSaveAll(List<OrderDetailDto> orderDetailDtoList){
        CompletableFuture.runAsync(() -> {
            for (OrderDetailDto orderDetailDto : orderDetailDtoList){
                OrderEntity orderEntity = OrderEntity.builder().orderId(orderDetailDto.getOrderId()).build();
                ProductEntity productEntity = productService.getProductEntity(orderDetailDto.getProductDto().getProductId());

                orderDetailRepository.save(
                        OrderDetailEntity.builder()
                                .orderEntity(orderEntity)
                                .amount(orderDetailDto.getAmount())
                                .productEntity(productEntity)
                                .build()
                );
            }
        });
    }

    public List<OrderDetailEntity> getOrderDetailsByOrderEntities(List<OrderEntity> orderEntities) {
        return orderDetailRepository.findAllByOrderEntityIn(orderEntities);
    }

    public void rollbackOrder(List<OrderDetailDto> orderDetailDtoList) {
        for (OrderDetailDto orderDetailDto : orderDetailDtoList) {
            stockService.repairStock(orderDetailDto);
            stockService.updateStockInRedis(orderDetailDto.getProductDto().getProductId(), -orderDetailDto.getAmount());
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
        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

        List<OrderDetailEntity> orderDetailEntities = orderDetailRepository.findByOrderEntity(orderEntity);

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            OrderDetailDto orderDetailDto = orderDetailEntity.toDto();
            orderDetailDto.setProductDto(productService.getProductDto(orderDetailEntity.getProductEntity()));
            orderDetailDtoList.add(orderDetailDto);
        }

        return orderDetailDtoList;
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
