package com.daedan.festabook.global.logging.dto;

public record ExceptionMessage(
        String type,
        int errorCode,
        String exceptionMessage,
        String stackTrace
) {
}
