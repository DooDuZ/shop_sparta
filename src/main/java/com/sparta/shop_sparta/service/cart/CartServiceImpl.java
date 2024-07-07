package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.constant.cart.CartResponseMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.dto.cart.CartRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.exception.CartException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.memoryRepository.CartRedisRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import com.sparta.shop_sparta.service.product.StockService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRedisRepository cartRedisRepository;
    private final ProductService productService;

    private final StockService stockService;

    @Override
    @Transactional
    public void createCart(MemberEntity memberEntity) {
        cartRedisRepository.saveWithDuration(memberEntity.getMemberId(), 0L, 0L);
    }

    @Override
    public ProductDto addProductToCart(UserDetails userDetails, CartRequestDto cartRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(!cartRedisRepository.hasKey(memberEntity.getMemberId())){
            createCart(memberEntity);
        }

        // 상품 유효성 검증
        ProductEntity productEntity = validateProduct(cartRequestDto.getProductId());
        StockEntity stockEntity = stockService.getStockEntity(productEntity);

        if(stockEntity.getAmount() < cartRequestDto.getAmount()){
            throw new ProductException(ProductMessage.OUT_OF_STOCK.getMessage());
        }

        cartRedisRepository.saveWithDuration(
                memberEntity.getMemberId(),
                cartRequestDto.getProductId(),
                cartRequestDto.getAmount()
        );

        ProductDto productDto = productEntity.toDto();
        productDto.setAmount(stockEntity.getAmount());

        return productDto;
    }

    private ProductEntity validateProduct(Long productId){
        // 조회 실패시 exception
        ProductEntity productEntity = productService.getProductEntity(productId);

        if(productEntity.getProductStatus() != ProductStatus.ON_SALE){
            throw new CartException(CartResponseMessage.NOT_ON_SALE.getMessage());
        }

        return productEntity;
    }

    // client 입장에선 create 여부는 중요하지 않다
    // 그냥 장바구니 '줘' 하면 줘야함
    @Override
    public CartDto getCart(UserDetails userDetails) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberEntity.getMemberId());
        List<ProductDto> productDtoList = getProductsIncart(cartInfo);
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

    List<ProductDto> getProductsIncart(Map<Long, Long> cartInfo){
        return productService.getProductDtoList(cartInfo);
    }

    @Override
    @Transactional
    public void removeOrderedProduct(MemberEntity memberEntity, List<OrderDetailDto> orderDetails) {
        CartDto cartDto = getCart(memberEntity);
        Long memberId = memberEntity.getMemberId();

        for (OrderDetailDto orderedProduct : orderDetails) {
            cartRedisRepository.removeCartDetail(memberId, orderedProduct.getProductDto().getProductId());
        }
    }

    @Override
    public void removeCartDetail(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        cartRedisRepository.removeCartDetail(memberEntity.getMemberId(), productId);
    }

    @Override
    public void updateCartDetail(UserDetails userDetails, CartRequestDto cartRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        Long memberId = memberEntity.getMemberId();
        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberId);

        if (cartInfo.containsKey(cartRequestDto.getProductId())) {
            cartRedisRepository.saveWithDuration(memberId, cartRequestDto.getProductId(), cartRequestDto.getAmount());
        }
    }
}
