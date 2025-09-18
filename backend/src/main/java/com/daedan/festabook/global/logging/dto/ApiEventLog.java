package com.daedan.festabook.global.logging.dto;

public record ApiEventLog(
        LogType type,
        String httpMethod,
        String uri,
        String ipAddress,
        String username
) {

    public static ApiEventLog from(String httpMethod, String uri, String ipAddress, String username) {
        return new ApiEventLog(
                LogType.API_EVENT,
                httpMethod,
                uri,
                ipAddress,
                username
        );
    }
}
