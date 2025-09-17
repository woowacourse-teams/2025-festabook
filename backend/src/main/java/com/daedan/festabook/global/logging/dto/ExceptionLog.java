package com.daedan.festabook.global.logging.dto;

public record ExceptionLog(
        String type,
        int errorCode,
        String exceptionMessage,
        String stackTrace
) {
}
