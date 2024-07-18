package com.sparta.shop_sparta.exception;

import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException{

    private final MemberResponseMessage error;

    public MemberException(MemberResponseMessage error) {
        super(error.getMessage());
        this.error = error;
    }

    public MemberException(MemberResponseMessage error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }
}
