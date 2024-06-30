package com.sparta.shop_sparta.service.member.addr;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.MemberException;
import com.sparta.shop_sparta.repository.AddrRepository;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddrServiceImpl implements AddrService {
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;
    private final AddrRepository addrRepository;

    @Override
    public ResponseEntity<?> addAddr(UserDetails userDetails, AddrDto addrDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (addrRepository.findAllByMemberEntity_MemberId(memberEntity.getMemberId()).size() >= 5){
            throw new MemberException(MemberResponseMessage.MAX_SAVE_LIMIT.getMessage());
        }

        String salt = saltGenerator.generateSalt();

        AddrEntity addrEntity = AddrEntity.builder()
                .memberEntity(memberEntity)
                .addr(userInformationEncoder.encrypt(addrDto.getAddr(), salt))
                .addrDetail(userInformationEncoder.encrypt(addrDto.getAddrDetail(), salt))
                .build();

        addrRepository.save(addrEntity);

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> removeAddr(UserDetails userDetails, Long addrId) {
        getAuthorizedAddrEntity(userDetails, addrId);
        addrRepository.deleteById(addrId);

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateAddr(UserDetails userDetails, AddrDto addrDto) {
        AddrEntity addrEntity = getAuthorizedAddrEntity(userDetails, addrDto.getAddrId());

        String salt = saltGenerator.generateSalt();

        addrEntity.setAddr(userInformationEncoder.encrypt(addrDto.getAddr(), salt));
        addrEntity.setAddrDetail(userInformationEncoder.encrypt(addrDto.getAddrDetail(), salt));

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> getAddr(UserDetails userDetails, Long addrId) {
        AddrEntity addrEntity = getAuthorizedAddrEntity(userDetails, addrId);

        AddrDto addrDto = addrEntity.toDto();

        addrDto.setAddr(userInformationEncoder.decrypt(addrEntity.getAddr()));
        addrDto.setAddrDetail(userInformationEncoder.decrypt(addrEntity.getAddrDetail()));

        return ResponseEntity.ok().body(addrDto);
    }

    @Override
    public List<AddrDto> getAddrList(Long memberId) {
        return addrRepository.findAllByMemberEntity_MemberId(memberId).stream()
                .map(AddrEntity::toDto).map(this::decryptAddrDto)
                .toList();
    }

    private AddrDto decryptAddrDto(AddrDto addrDto) {
        addrDto.setAddr(userInformationEncoder.decrypt(addrDto.getAddr()));
        addrDto.setAddrDetail(userInformationEncoder.decrypt(addrDto.getAddrDetail()));

        return addrDto;
    }


    private AddrEntity getAuthorizedAddrEntity(UserDetails userDetails, Long addrId){
        AddrEntity addrEntity = addrRepository.findById(addrId).orElseThrow(
                () -> new MemberException(MemberResponseMessage.NOT_SEARCH_ADDR.getMessage())
        );

        if(!addrEntity.getMemberEntity().getLoginId().equals(userDetails.getUsername())){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        return addrEntity;
    }
}
