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
public class AddrDTO {
    private Long addrId;
    private String addr;
    private String addr_detail;
    private Long memberId;

    public AddrEntity toEntity() {
        return AddrEntity.builder().addrId(this.addrId).addr(this.addr).addr_detail(this.addr_detail).build();
    }
}
