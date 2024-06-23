package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.List;

public interface AddrService {
    void addAddr(MemberEntity memberEntity, String addr, String addrDetail);
    void removeAddr(Long addrId);
    void updateAddr(Long addrId, String addr, String addrDetail);
    AddrDto getAddr(Long addrId);
    List<AddrDto> getAddrList(Long memberId);
}
