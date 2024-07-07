package com.sparta.shop_sparta.service.member.addr;

import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AddrService {
    void addAddr(UserDetails userDetails, AddrDto addrDto);
    void removeAddr(UserDetails userDetails, Long addrId);
    void updateAddr(UserDetails userDetails, AddrDto addrDto);
    AddrDto getAddr(UserDetails userDetails, Long addrId);
    List<AddrDto> getAddrList(UserDetails userDetails, Long memberId);
}
