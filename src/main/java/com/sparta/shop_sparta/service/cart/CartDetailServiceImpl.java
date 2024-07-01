package com.sparta.shop_sparta.service.cart;


import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.repository.CartDetailRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartDetailServiceImpl implements CartDetailService {

    private final CartDetailRepository cartDetailRepository;
    private final ProductService productService;

    @Override
    public List<CartDetailResponseDto> getCartDetailsByCartEntity(CartEntity cartEntity) {
        return List.of();
    }

    public List<CartDetailResponseDto> mapToCartDetailDtoList(Map<Long, Long> cartDetailMap) {
        List<CartDetailResponseDto> cartDetailRequestDtoList = new ArrayList<>();

        for (Long key : cartDetailMap.keySet()) {
            // 빈 카트 저장을 위한 더미 값 == 0
            if(key == 0){
                continue;
            }

            cartDetailRequestDtoList.add(
                    CartDetailResponseDto.builder()
                            .productResponseDto(productService.getProductResponseDto(key))
                            .amount(cartDetailMap.get(key))
                            .build());
        }

        return cartDetailRequestDtoList;
    }

    public void validateProduct(Long productId){
        // 조회 실패시 exception
        productService.getProductEntity(productId);
    }
}
