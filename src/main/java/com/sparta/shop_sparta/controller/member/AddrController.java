package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AddrController {
    ResponseEntity<?> addAddr(UserDetails userDetails, AddrDto addrDto);
    ResponseEntity<?> removeAddr(UserDetails userDetails, Long addrId);
    ResponseEntity<?> updateAddr(UserDetails userDetails, AddrDto addrDto);
    ResponseEntity<?> getAddr(UserDetails userDetails, Long addrId);
    List<AddrDto> getAddrList(Long memberId);
}
