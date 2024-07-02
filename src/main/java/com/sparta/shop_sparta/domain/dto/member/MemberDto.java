package com.sparta.shop_sparta.domain.dto.member;


import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class MemberDto {
    private Long memberId;

    @NotBlank(message = "아이디는 필수 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$", message = "알파벳 포함, 알파벳 + 숫자 5-25자")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$", message = "영 대/소문자, 숫자, 특수문자 필수 포함 10-16자")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String memberName;

    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호 양식을 맞춰주세요.")
    private String phoneNumber;

    @NotNull(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank
    private String addr;
    @NotBlank
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

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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
}
