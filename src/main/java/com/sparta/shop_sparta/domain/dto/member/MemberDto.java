package com.sparta.shop_sparta.domain.dto.member;


import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto implements UserDetails {
    private Long memberId;
    private String loginId;
    private String password;
    private String memberName;
    private String phoneNumber;
    private String email;
    private String addr;
    private String addrDetail;
    private MemberRole role;

    private Set<GrantedAuthority> authorities;


    public MemberEntity toEntity() {
        return MemberEntity.builder().memberId(this.memberId).loginId(this.loginId).password(this.password)
                .memberName(this.memberName).phoneNumber(this.phoneNumber).email(this.email).role(this.role).build();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }


    public void setAuthorities(Set<GrantedAuthority> authorities){
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return loginId;
    }


}
