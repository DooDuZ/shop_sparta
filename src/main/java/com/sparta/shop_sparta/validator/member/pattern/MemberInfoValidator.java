package com.sparta.shop_sparta.validator.member.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberInfoValidator implements PatternValidator{

    private final Pattern pattern;

    public MemberInfoValidator(Pattern pattern){
        this.pattern = pattern;
    }

    @Override
    public Boolean checkPattern(String info) {
        Matcher matcher = pattern.matcher(info);
        return matcher.matches();
    }
}
