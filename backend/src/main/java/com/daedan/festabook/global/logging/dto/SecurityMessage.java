package com.daedan.festabook.global.logging.dto;

public record SecurityMessage(
        String type,
        String requestURI,
        String httpMethod,
        String exceptionMessage

) {
}
