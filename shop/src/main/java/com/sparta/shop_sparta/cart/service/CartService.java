package com.sparta.shop_sparta.cart.service;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.cart.domain.dto.CartDetailResponseDto;
import com.sparta.shop_sparta.cart.domain.dto.CartRequestDto;
import com.sparta.shop_sparta.cart.domain.dto.CartDto;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.cart.repository.CartRedisRepository;
import com.sparta.shop_sparta.product.service.ProductService;
import com.sparta.shop_sparta.product.service.StockService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final ProductService customerProductService;

    private final StockService stockService;

    @Transactional
    public void createCart(MemberEntity memberEntity) {
        cartRedisRepository.saveWithDuration(memberEntity.getMemberId(), 0L, 0L);
    }

    public void addProductToCart(UserDetails userDetails, CartRequestDto cartRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(!cartRedisRepository.hasKey(memberEntity.getMemberId())){
            createCart(memberEntity);
        }

        // 상품 유효성 검증
        ProductEntity productEntity = validateProduct(cartRequestDto.getProductId());
        StockEntity stockEntity = stockService.getStockEntity(productEntity);

        /* Todo 테스트를 위한 임시 주석
        if(stockEntity.getAmount() < cartRequestDto.getAmount()){
            throw new ProductException(ProductMessage.OUT_OF_STOCK);
        }*/

        cartRedisRepository.saveWithDuration(
                memberEntity.getMemberId(),
                cartRequestDto.getProductId(),
                cartRequestDto.getAmount()
        );
    }

    private ProductEntity validateProduct(Long productId){
        // 조회 실패시 exception
        ProductEntity productEntity = customerProductService.getProductEntity(productId);

        if(productEntity.getProductStatus() != ProductStatus.ON_SALE){
            throw new ProductException(ProductMessage.NOT_ON_SALE);
        }

        return productEntity;
    }

    // client 입장에선 create 여부는 중요하지 않다
    // 그냥 장바구니 '줘' 하면 줘야함
    public CartDto getCart(UserDetails userDetails) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberEntity.getMemberId());
        List<ProductDto> productDtoList = getProductsInCart(cartInfo);
        //레디스에 cart가 존재하면 파싱해서 return
        if (!cartInfo.isEmpty()) {
            CartDto cartDto = CartDto.builder().memberId(memberEntity.getMemberId()).build();

            List<CartDetailResponseDto> cartDetailResponseDtoList = new ArrayList<>();

            for (ProductDto productDto : productDtoList) {
                cartDetailResponseDtoList.add(CartDetailResponseDto.builder().productDto(productDto).amount(
                        cartInfo.get(productDto.getProductId())).build());
            }

            cartDto.setCartDetails(cartDetailResponseDtoList);

            return cartDto;
        }

        // 없으면 생성 후 return
        createCart(memberEntity);

        return CartDto.builder().memberId(memberEntity.getMemberId()).build();
    }

    public Map<Long, Long> getCartInfo(UserDetails userDetails) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(!cartRedisRepository.hasKey(memberEntity.getMemberId())){
            // 없으면 생성 후 return
            createCart(memberEntity);
        }

        return cartRedisRepository.findCart(memberEntity.getMemberId());
    }

    List<ProductDto> getProductsInCart(Map<Long, Long> cartInfo){
        return customerProductService.getProductDtoList(cartInfo);
    }

    @Transactional
    public void removeOrderedProduct(MemberEntity memberEntity, List<OrderDetailRequestDto> orderDetailEntities) {
        Long memberId = memberEntity.getMemberId();

        for (OrderDetailRequestDto orderedProduct : orderDetailEntities) {
            cartRedisRepository.removeCartDetail(memberId, orderedProduct.getProductId());
        }
    }

    public void removeCartDetail(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        cartRedisRepository.removeCartDetail(memberEntity.getMemberId(), productId);
    }

    public void updateCartDetail(UserDetails userDetails, CartRequestDto cartRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        Long memberId = memberEntity.getMemberId();
        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberId);

        if (cartInfo.containsKey(cartRequestDto.getProductId())) {
            cartRedisRepository.saveWithDuration(memberId, cartRequestDto.getProductId(), cartRequestDto.getAmount());
        }
    }
}
