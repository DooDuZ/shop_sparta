package com.sparta.shop_sparta.validator.member.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberInfoValidator implements PatternValidator{

    public MemberInfoValidator(){}

    @Override
    public Boolean checkPattern(Pattern pattern, String info) {
        Matcher matcher = pattern.matcher(info);
        return matcher.matches();
    }
}
