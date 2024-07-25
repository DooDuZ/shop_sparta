package com.sparta.shop_sparta.util.validator.member.pattern;

public class PasswordValidatorImpl extends MemberInfoValidator implements PasswordValidator{
    public PasswordValidatorImpl() {
        super();
    }

    @Override
    public Boolean checkSame(String password, String newPassword) {
        return password.equals(newPassword);
    }
}
