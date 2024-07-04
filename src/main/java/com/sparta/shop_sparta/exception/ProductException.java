package com.sparta.shop_sparta.exception;

public class ProductException extends RuntimeException{
    public ProductException(String message){super(message);}
    public ProductException(String message, Throwable cause){super(message, cause);}
    public ProductException(Throwable cause){super(cause);}
}
