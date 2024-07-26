package com.sparta.shop_sparta.member.domain.entity;

import com.sparta.common.constant.member.MemberRole;
import com.sparta.shop_sparta.member.domain.dto.MemberDto;
import com.sparta.shop_sparta.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "member")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity extends BaseEntity implements UserDetails {
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

    @Transient
    private Set<GrantedAuthority> authorities;


    public MemberDto toDto() {
        return MemberDto.builder().memberId(this.memberId).email(this.email).memberName(this.memberName)
                .loginId(this.loginId).phoneNumber(this.phoneNumber).role(this.role).build();
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
