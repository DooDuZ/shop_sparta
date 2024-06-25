package com.sparta.shop_sparta.domain.entity.member;

import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    // 레코드 생성 후 role 삽입
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public MemberDto toDto() {
        return MemberDto.builder().memberId(this.memberId).email(this.email).memberName(this.memberName)
                .loginId(this.loginId).phoneNumber(this.phoneNumber).role(this.role).password(this.password).build();
    }
}
