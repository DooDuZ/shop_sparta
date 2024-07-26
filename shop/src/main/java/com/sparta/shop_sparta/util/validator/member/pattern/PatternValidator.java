package com.sparta.shop_sparta.util.validator.member.pattern;

import java.util.regex.Pattern;

public interface PatternValidator {
    Boolean checkPattern(Pattern pattern, String data);
}
