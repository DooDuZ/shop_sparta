package com.sparta.shop_sparta.exception;

import com.sparta.shop_sparta.constant.product.ProductMessage;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException{

    private final ProductMessage error;

    public ProductException(ProductMessage error){
        super(error.getMessage());
        this.error = error;
    }
    public ProductException(ProductMessage error, Throwable cause){
        super(error.getMessage(), cause);
        this.error = error;
    }
}
