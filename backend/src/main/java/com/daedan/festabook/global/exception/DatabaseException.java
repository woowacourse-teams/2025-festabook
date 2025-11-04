package com.daedan.festabook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DatabaseException extends BusinessException {

    private final String originalExceptionMessage;

    protected DatabaseException(String message, String originalExceptionMessage) {
        super(message, HttpStatus.BAD_REQUEST);
        this.originalExceptionMessage = originalExceptionMessage;
    }
}
