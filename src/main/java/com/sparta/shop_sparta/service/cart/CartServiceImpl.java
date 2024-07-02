package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.constant.cart.CartResponseMessage;
import com.sparta.shop_sparta.constant.cart.CartStatus;
import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.entity.cart.CartDetailEntity;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.CartException;
import com.sparta.shop_sparta.repository.CartDetailRepository;
import com.sparta.shop_sparta.repository.CartRepository;
import com.sparta.shop_sparta.repository.memoryRepository.CartRedisRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Transactional
    public CartEntity createCart(MemberEntity memberEntity) {
        CartEntity cartEntity = cartRepository.save(
                CartEntity.builder().memberEntity(memberEntity).cartStatus(CartStatus.UNORDERED).build()
        );

        // cartRedisRepository.addKey(cartEntity.getMemberEntity().getMemberId());
        cartRedisRepository.save(cartEntity.getMemberEntity().getMemberId(), 0L, 0L);

        return cartEntity;
    }

    @Override
    public ResponseEntity<?> createCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(!cartRedisRepository.hasKey(memberEntity.getMemberId())){
            Optional<CartEntity> optional = cartRepository.findByMemberEntityAndCartStatus(memberEntity, CartStatus.UNORDERED);
            CartEntity cartEntity = optional.orElse(null);

            if(cartEntity == null){
                cartEntity = createCart(memberEntity);
            }

            addCartToRedis(cartEntity);
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

        Optional<CartEntity> optional = cartRepository.findByMemberEntityAndCartStatus(memberEntity, CartStatus.UNORDERED);

        // 주문 전인 카트 정보가 존재하면
        if (optional.isPresent()) {
            return ResponseEntity.ok(getCart(memberEntity, optional.get()));
        }

        // 없으면 만들어서 return
        return ResponseEntity.ok(createCart(memberEntity).toDto());
    }

    // 메서드 overloading
    private CartDto getCart(MemberEntity memberEntity, CartEntity cartEntity) {
        if (memberEntity.getMemberId() - cartEntity.getMemberEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberEntity.getMemberId());

        //레디스에 cart가 존재하면 파싱해서 return
        if (!cartInfo.isEmpty()) {
            CartDto cartDto = cartEntity.toDto();
            cartDto.setCartDetails(cartDetailService.mapToCartDetailDtoList(cartInfo));

            return cartDto;
        }

        CartDto cartDto = cartEntity.toDto();
        List<CartDetailResponseDto> cartDetailResponseDtoList = cartDetailService.getCartDetailResponseByCartEntity(cartEntity);
        cartDto.setCartDetails(cartDetailResponseDtoList);

        addCartToRedis(cartEntity);

        return cartDto;
    }

    public Map<Long, Long> getCartInRedis(MemberEntity memberEntity){
        Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberEntity.getMemberId());

        if (cartInfo.isEmpty()) {
            Optional<CartEntity> optional = cartRepository.findByMemberEntityAndCartStatus(memberEntity, CartStatus.UNORDERED);

            if (optional.isPresent()) {
                addCartToRedis(optional.get());
            }
        }

        return cartRedisRepository.findCart(memberEntity.getMemberId());
    }

    @Override
    @Transactional
    public void removeOrderedProduct(MemberEntity memberEntity, List<OrderDetailDto> orderDetails) {
        Map<Long, Long> cartInfo = getCartInRedis(memberEntity);
        Long memberId = memberEntity.getMemberId();

        CartEntity cartEntity = cartRepository.findByMemberEntityAndCartStatus(memberEntity,
                CartStatus.UNORDERED).orElseThrow(
                () -> new CartException(CartResponseMessage.INVALID_CART_ID.getMessage())
        );

        for (OrderDetailDto orderedProduct : orderDetails) {
            cartRedisRepository.removeCartDetail(memberId, orderedProduct.getProductId());
            cartDetailService.removeOrderedProduct(cartEntity, orderedProduct.getProductId());
        }

        // 장바구니의 모든 상품이 주문 됐다면 현재 카트는 주문 완료 처리
        if(cartRedisRepository.findCart(memberId).size() == 1){
            cartEntity.setCartStatus(CartStatus.ORDERED);
            cartRedisRepository.removeKey(memberId);

            createCart(memberEntity);
        }
    }


    private void addCartToRedis(CartEntity cartEntity) {
        MemberEntity memberEntity = cartEntity.getMemberEntity();
        Long memberId = memberEntity.getMemberId();

        List<CartDetailEntity> cartDetailEntities = cartDetailService.getCartDetailsByCartEntity(cartEntity);

        cartRedisRepository.save(memberId, 0L, 0L);

        for(CartDetailEntity cartDetailEntity : cartDetailEntities) {
            cartRedisRepository.save(
                    memberId,
                    cartDetailEntity.getProductEntity().getProductId(),
                    cartDetailEntity.getAmount());
        }
        cartDetailService.removeCart(cartEntity);
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

    @Transactional
    //@Scheduled(cron = "0 44 3 * * ?")
    @Scheduled(fixedRate = 120000)
    public void flushRedisRepository(){
        Set<Long> cartKeys = cartRedisRepository.getAllCartKeys();

        for (Long memberId : cartKeys) {
            CartEntity cartEntity = cartRepository.findByMemberEntity_MemberIdAndCartStatus(memberId, CartStatus.UNORDERED).orElseThrow(
                    () -> new CartException(CartResponseMessage.INVALID_CART_ID.getMessage())
            );

            Map<Long, Long> cartInfo = cartRedisRepository.findCart(memberId);
            cartDetailService.addCartDetail(cartEntity, cartInfo);
        }

        cartRedisRepository.deleteAllCartKeys();
    }
}
