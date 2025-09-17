package com.daedan.festabook.global.logging.dto;

public record ApiEventLog(
        String type,
        String httpMethod,
        String uri
) {
}
