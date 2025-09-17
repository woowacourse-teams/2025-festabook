package com.daedan.festabook.global.logging.dto;

public record ApiEventLog(
        LogType type,
        String httpMethod,
        String uri
) {

    public static ApiEventLog from(String httpMethod, String uri) {
        return new ApiEventLog(
                LogType.API_EVENT,
                httpMethod,
                uri
        );
    }
}
