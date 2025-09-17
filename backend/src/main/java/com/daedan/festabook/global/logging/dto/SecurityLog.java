package com.daedan.festabook.global.logging.dto;

public record SecurityLog(
        String type,
        String requestURI,
        String httpMethod,
        String exceptionMessage

) {
}
