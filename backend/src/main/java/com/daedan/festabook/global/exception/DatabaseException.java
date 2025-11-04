package com.daedan.festabook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DatabaseException extends BusinessException {

    private final String originalMessage;

    protected DatabaseException(String message, String originalMessage) {
        super(message, HttpStatus.BAD_REQUEST);
        this.originalMessage = originalMessage;
    }
}
