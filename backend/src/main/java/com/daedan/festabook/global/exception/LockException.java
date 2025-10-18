package com.daedan.festabook.global.exception;

import org.springframework.http.HttpStatus;

public class LockException extends BusinessException {

    public LockException(String key, String message, HttpStatus httpStatus) {
        this(String.format("[%s] %s", key, message), httpStatus);
    }

    protected LockException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
