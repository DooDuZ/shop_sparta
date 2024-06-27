package com.sparta.shop_sparta.domain.dto.member;

import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddrDto {
    private Long addrId;
    private String addr;
    private String addrDetail;
    private Long memberId;

    public AddrEntity toEntity() {
        return AddrEntity.builder().addrId(this.addrId).addr(this.addr).addr_detail(this.addrDetail).build();
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }
}
