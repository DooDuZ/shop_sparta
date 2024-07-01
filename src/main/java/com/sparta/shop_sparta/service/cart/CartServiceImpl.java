package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.constant.cart.CartResponseMessage;
import com.sparta.shop_sparta.constant.cart.CartStatus;
import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.CartException;
import com.sparta.shop_sparta.repository.CartRepository;
import com.sparta.shop_sparta.repository.memoryRepository.CartRedisRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartRedisRepository cartRedisRepository;
    private final CartDetailService cartDetailService;

    @Override
    public CartDto createCart(MemberEntity memberEntity) {
        CartEntity cartEntity = cartRepository.save(
                CartEntity.builder().memberEntity(memberEntity).cartStatus(CartStatus.UNORDERED).build()
        );

        cartRedisRepository.addKey(cartEntity.getCartId());

        return cartEntity.toDto();
    }

    @Override
    public ResponseEntity<?> createCartDetail(UserDetails userDetails, Long cartId, CartDetailDto cartDetailDto) {
        return null;
    }

    // client 입장에선 create 여부는 중요하지 않다
    // 그냥 장바구니 '줘' 하면 줘야함
    @Override
    public ResponseEntity<CartDto> getCart(UserDetails userDetails) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Optional<CartEntity> optional = cartRepository.findByMemberEntityAndCartStatus(memberEntity,
                CartStatus.UNORDERED);

        // 주문 완료된 카트 정보가 존재하면
        if (optional.isPresent()) {
            return getCart(memberEntity, optional.get());
        }

        // 없으면 만들어서 return
        return ResponseEntity.ok(createCart(memberEntity));
    }

    // 내부 사용 메서드 overloading
    private ResponseEntity<CartDto> getCart(MemberEntity memberEntity, CartEntity cartEntity) {
        if (memberEntity.getMemberId() - cartEntity.getMemberEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        Map<Long, Long> cartInfo = cartRedisRepository.findCart(cartEntity.getCartId());

        //레디스에 cart가 존재하면 파싱해서 return
        if (!cartInfo.isEmpty()) {
            CartDto cartDto = cartEntity.toDto();
            cartDto.setCartDetails(mapToCartDetailDtoList(cartInfo));
            // return ResponseEntity.ok(optional.get().toDto());
            return ResponseEntity.ok(cartDto);
        }

        CartDto cartDto = cartEntity.toDto();
        List<CartDetailDto> cartDetailDtoList = cartDetailService.getCartDetailsByCartEntity(cartEntity);
        cartDto.setCartDetails(cartDetailDtoList);

        Long cartId = cartEntity.getCartId();

        // cart가 로드될 때 redis에 등록
        for(CartDetailDto cartDetailDto : cartDetailDtoList) {
            cartRedisRepository.save(cartId, cartDetailDto.getProductId(), cartDetailDto.getAmount());
        }

        return ResponseEntity.ok(cartDto);
    }

    private List<CartDetailDto> mapToCartDetailDtoList(Map<Long, Long> cartDetailMap) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        for (Long key : cartDetailMap.keySet()) {
            cartDetailDtoList.add( CartDetailDto.builder().productId(key).amount(cartDetailMap.get(key)).build() );
        }

        return cartDetailDtoList;
    }

    @Override
    public ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long cartDetailId) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateCartDetail(UserDetails userDetails, Long cartDetailId, CartDetailDto cartDetailDto) {
        return null;
    }
}
