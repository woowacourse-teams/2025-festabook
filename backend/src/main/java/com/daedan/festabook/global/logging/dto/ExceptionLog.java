package com.daedan.festabook.global.logging.dto;

public record ExceptionLog(
        LogType type,
        int errorCode,
        String exceptionMessage,
        String exceptionClass,
        String stackTrace
) {

    public static ExceptionLog from(
            int errorCode,
            String exceptionMessage,
            String exceptionClass,
            String stackTrace
    ) {
        return new ExceptionLog(
                LogType.EXCEPTION,
                errorCode,
                exceptionMessage,
                exceptionClass,
                stackTrace
        );
    }
}
