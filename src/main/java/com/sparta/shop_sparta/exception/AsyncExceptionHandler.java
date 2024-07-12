package com.sparta.shop_sparta.exception;

import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(AsyncExceptionHandler.class.getName());

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        LOGGER.info("exception occurred in async method: " + method.getName());
    }
}
