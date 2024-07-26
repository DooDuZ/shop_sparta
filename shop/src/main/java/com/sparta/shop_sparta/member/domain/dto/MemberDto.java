package com.sparta.shop_sparta.member.domain.dto;


import com.sparta.common.constant.member.MemberRole;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
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
    @Pattern(regexp = "^[A-z0-9]{2,20}+@[A-z]{2,20}+\\.[a-z]{2,3}$", message = "유효한 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank
    private String addr;
    private String addrDetail;

    private MemberRole role;

    private Set<GrantedAuthority> authorities;


    public MemberEntity toEntity() {
        return MemberEntity.builder().memberId(this.memberId).loginId(this.loginId).password(this.password)
                .memberName(this.memberName).phoneNumber(this.phoneNumber).email(this.email).role(this.role).build();
    }
}
