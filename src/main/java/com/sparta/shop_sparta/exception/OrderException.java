package com.sparta.shop_sparta.exception;

public class OrderException extends RuntimeException {


    public OrderException(String message) {super(message);}
    public OrderException(String message, Throwable cause) { super(message, cause); }
    public OrderException(Throwable cause) { super(cause);}
}
