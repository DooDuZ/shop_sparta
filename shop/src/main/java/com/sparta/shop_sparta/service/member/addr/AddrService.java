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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddrService {
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;
    private final AddrRepository addrRepository;

    public void addAddr(UserDetails userDetails, AddrDto addrDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (addrRepository.findAllByMemberEntity(memberEntity).size() >= 5){
            throw new MemberException(MemberResponseMessage.MAX_SAVE_LIMIT);
        }

        String salt = saltGenerator.generateSalt();

        AddrEntity addrEntity = AddrEntity.builder()
                .memberEntity(memberEntity)
                .addr(userInformationEncoder.encrypt(addrDto.getAddr(), salt))
                .addrDetail(userInformationEncoder.encrypt(addrDto.getAddrDetail(), salt))
                .build();

        addrRepository.save(addrEntity);
    }

    @Transactional
    public void removeAddr(UserDetails userDetails, Long addrId) {
        getAuthorizedAddrEntity(userDetails, addrId);
        addrRepository.deleteById(addrId);
    }

    @Transactional
    public void updateAddr(UserDetails userDetails, AddrDto addrDto) {
        AddrEntity addrEntity = getAuthorizedAddrEntity(userDetails, addrDto.getAddrId());

        String salt = saltGenerator.generateSalt();

        addrEntity.setAddr(userInformationEncoder.encrypt(addrDto.getAddr(), salt));
        addrEntity.setAddrDetail(userInformationEncoder.encrypt(addrDto.getAddrDetail(), salt));
    }

    public AddrDto getAddr(UserDetails userDetails, Long addrId) {
        AddrEntity addrEntity = getAuthorizedAddrEntity(userDetails, addrId);

        AddrDto addrDto = addrEntity.toDto();

        addrDto.setAddr(userInformationEncoder.decrypt(addrEntity.getAddr()));
        addrDto.setAddrDetail(userInformationEncoder.decrypt(addrEntity.getAddrDetail()));

        return addrDto;
    }

    public List<AddrDto> getAddrList(UserDetails userDetails, Long memberId) {
        return addrRepository.findAllByMemberEntity( (MemberEntity) userDetails).stream()
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
                () -> new MemberException(MemberResponseMessage.NOT_SEARCH_ADDR)
        );

        if(!addrEntity.getMemberEntity().getLoginId().equals(userDetails.getUsername())){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        return addrEntity;
    }
}
