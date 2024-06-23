package com.sparta.shop_sparta.domain.dto.member;


import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {
    private Long memberId;
    private String loginId;
    private String password;
    private String memberName;
    private String phoneNumber;
    private String email;
    private String addr;
    private String addrDetail;
    private MemberRole role;


    public MemberEntity toEntity() {
        return MemberEntity.builder().memberId(this.memberId).loginId(this.loginId).password(this.password)
                .memberName(this.memberName).phoneNumber(this.phoneNumber).email(this.email).role(this.role).build();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
