package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.repository.AddrRepository;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddrServiceImpl implements AddrService{
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;
    private final AddrRepository addrRepository;

    @Override
    public void addAddr(MemberEntity memberEntity, String addr, String addrDetail) {
        String salt = saltGenerator.generateSalt();

        AddrEntity addrEntity = AddrEntity.builder()
                .memberEntity(memberEntity)
                .addr(userInformationEncoder.encrypt(addr, salt))
                .addr_detail(userInformationEncoder.encrypt(addrDetail, salt))
                .build();

        addrRepository.save(addrEntity);
    }

    @Override
    public void removeAddr(Long addrId) {
        addrRepository.deleteById(addrId);
    }

    @Override
    public void updateAddr(Long addrId, String addr, String addrDetail) {

    }

    @Override
    public AddrDto getAddr(Long addrId) {
        return null;
    }

    @Override
    public List<AddrDto> getAddrList(Long memberId) {
        return null;
    }
}
