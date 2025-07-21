package com.daedan.festabook.global.exception;

public record ExceptionResponse(
        String message
) {

    public static ExceptionResponse from(String message) {
        return new ExceptionResponse(message);
    }
}
