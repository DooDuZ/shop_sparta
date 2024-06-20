package com.sparta.shop_sparta.domain.entity.member;

import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "addr")
@NoArgsConstructor
@Getter
public class AddrEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addrId;
    @Column(nullable = false)
    private String addr;
    @Column(nullable = false)
    private String addr_detail;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "memberId")
    MemberEntity memberEntity;

    public AddrEntity(String addr, String addr_detail, MemberEntity memberEntity) {
        this.addr = addr;
        this.addr_detail = addr_detail;
        this.memberEntity = memberEntity;
    }
}
