package com.daedan.festabook.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends BusinessException {

    private final static String message = "권한이 없습니다.";

    public ForbiddenException() {
        super(message, HttpStatus.FORBIDDEN);
    }
}
