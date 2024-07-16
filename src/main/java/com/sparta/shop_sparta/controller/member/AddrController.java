package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.service.member.addr.AddrService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AddrController {

    private final AddrService addrService;

    @PostMapping("/{memberId}/addr")
    public ResponseEntity<Void> addAddr(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddrDto addrDto) {
        addrService.addAddr(userDetails, addrDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/addr/{addrId}")
    public ResponseEntity<Void> removeAddr(@AuthenticationPrincipal UserDetails userDetails, Long addrId) {
        addrService.removeAddr(userDetails, addrId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/addr/{addrId}")
    public ResponseEntity<Void> updateAddr(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddrDto addrDto) {
        addrService.updateAddr(userDetails, addrDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/addr/{addrId}")
    public ResponseEntity<?> getAddr(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long addrId) {
        return ResponseEntity.ok(addrService.getAddr(userDetails, addrId));
    }

    @GetMapping("/{memberId}/addr")
    public List<AddrDto> getAddrList(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long memberId) {

        return addrService.getAddrList(userDetails, memberId);
    }
}
