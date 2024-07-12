package com.sparta.shop_sparta.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        // ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<String> handleOrderException(OrderException ex) {
        // ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getError().getMessage(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleAuthException(AuthorizationException ex) {
        // ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getError().getMessage(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleProductException(ProductException ex) {
        // ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getError().getMessage(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<String> handleMemberException(MemberException ex) {
        // ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getError().getMessage(), ex.getError().getHttpStatus());
    }
}
