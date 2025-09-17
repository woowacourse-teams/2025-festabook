package com.daedan.festabook.global.logging.dto;

public record SecurityLog(
        LogType type,
        String requestURI,
        String httpMethod,
        String exceptionMessage
) {
}
