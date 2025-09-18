package com.daedan.festabook.global.logging.dto;

public record ApiLog(
        LogType type,
        String httpMethod,
        String queryString,
        String uri,
        String ipAddress,
        String username,
        int httpStatusCode,
        Object requestBody,
        long executionTime
) {

    public static ApiLog from(
            String httpMethod,
            String queryString,
            String uri,
            String ipAddress,
            String username,
            int httpStatusCode,
            Object requestBody,
            long executionTime
    ) {
        return new ApiLog(
                LogType.API,
                httpMethod,
                queryString,
                uri,
                ipAddress,
                username,
                httpStatusCode,
                requestBody,
                executionTime
        );
    }
}
