package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.constant.cart.CartResponseMessage;
import com.sparta.shop_sparta.constant.cart.CartStatus;
import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.CartException;
import com.sparta.shop_sparta.repository.CartDetailRepository;
import com.sparta.shop_sparta.repository.CartRepository;
import com.sparta.shop_sparta.repository.memoryRepository.CartRedisRepository;
import java.lang.reflect.Member;
import java.util.HashMap;
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
    private final CartDetailRepository cartDetailRepository;

    @Override
    public CartDto createCart(MemberEntity memberEntity) {
        CartEntity cartEntity = cartRepository.save(
                CartEntity.builder().memberEntity(memberEntity).cartStatus(CartStatus.UNORDERED).build()
        );

        // cartRedisRepository.addKey(cartEntity.getMemberEntity().getMemberId());
        cartRedisRepository.save(cartEntity.getMemberEntity().getMemberId(), 0L, 0L);

        return cartEntity.toDto();
    }

    @Override
    public ResponseEntity<?> createCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(cartRedisRepository.findCart(memberEntity.getMemberId()).isEmpty()){
            CartEntity cartEntity = cartRepository.findByMemberEntityAndCartStatus(memberEntity,
                    CartStatus.UNORDERED).orElseThrow(
                    () -> new CartException(CartResponseMessage.INVALID_CART_ID.getMessage())
            );

            addToRedis(cartEntity);
        }

        // 상품 유효성 검증
        cartDetailService.validateProduct(cartDetailRequestDto.getProductId());

        cartRedisRepository.save(
                memberEntity.getMemberId(),
                cartDetailRequestDto.getProductId(),
                cartDetailRequestDto.getAmount()
        );

        return ResponseEntity.ok().build();
    }

    // client 입장에선 create 여부는 중요하지 않다
    // 그냥 장바구니 '줘' 하면 줘야함
    @Override
    public ResponseEntity<CartDto> getCart(UserDetails userDetails) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Optional<CartEntity> optional = cartRepository.findByMemberEntityAndCartStatus(memberEntity,
                CartStatus.UNORDERED);

        // 주문 전인 카트 정보가 존재하면
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

        Map<Long, Long> cartInfo = cartRedisRepository.findCart(cartEntity.getMemberEntity().getMemberId());

        //레디스에 cart가 존재하면 파싱해서 return
        if (!cartInfo.isEmpty()) {
            CartDto cartDto = cartEntity.toDto();
            cartDto.setCartDetails(cartDetailService.mapToCartDetailDtoList(cartInfo));
            // return ResponseEntity.ok(optional.get().toDto());
            return ResponseEntity.ok(cartDto);
        }

        CartDto cartDto = addToRedis(cartEntity);

        return ResponseEntity.ok(cartDto);
    }


    private CartDto addToRedis(CartEntity cartEntity){
        CartDto cartDto = cartEntity.toDto();
        List<CartDetailResponseDto> cartDetailResponseDtoList = cartDetailService.getCartDetailsByCartEntity(cartEntity);
        cartDto.setCartDetails(cartDetailResponseDtoList);

        Long memberId = cartEntity.getMemberEntity().getMemberId();

        // cart가 로드될 때 redis에 등록
        // cartRedisRepository.addKey(memberId); -> 깡통 map이 안들어감
        cartRedisRepository.save(memberId, 0L, 0L);

        for(CartDetailResponseDto cartDetailRequestDto : cartDetailResponseDtoList) {
            cartRedisRepository.save(
                    memberId,
                    cartDetailRequestDto.getProductResponseDto().getProductId(),
                    cartDetailRequestDto.getAmount());
        }

        return cartDto;
    }

    @Override
    public ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        cartRedisRepository.removeCartDetail(memberEntity.getMemberId(), productId);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> updateCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        Long memberId = memberEntity.getMemberId();
        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberId);

        if (cartInfo.containsKey(cartDetailRequestDto.getProductId())) {
            cartRedisRepository.save(memberId, cartDetailRequestDto.getProductId(), cartDetailRequestDto.getAmount());
        }

        return ResponseEntity.ok().build();
    }
}
