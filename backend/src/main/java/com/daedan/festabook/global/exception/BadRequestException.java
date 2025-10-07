package com.daedan.festabook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BadRequestException extends BusinessException {

    protected BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
