package com.daedan.festabook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DatabaseException extends BusinessException {

    private final String originalExceptionMessage;

    protected DatabaseException(String message, String originalExceptionMessage, HttpStatus status) {
        super(message, status);
        this.originalExceptionMessage = originalExceptionMessage;
    }
}
