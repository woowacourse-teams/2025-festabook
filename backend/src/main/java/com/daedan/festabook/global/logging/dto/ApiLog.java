package com.daedan.festabook.global.logging.dto;

public record ApiLog(
        String type,
        String httpMethod,
        String queryString,
        String uri,
        int httpStatusCode,
        Object requestBody,
        long executionTime
) {

    public static ApiLog from(
            String httpMethod,
            String queryString,
            String uri,
            int httpStatusCode,
            Object requestBody,
            long executionTime
    ) {
        return new ApiLog(
                "API",
                httpMethod,
                queryString,
                uri,
                httpStatusCode,
                requestBody,
                executionTime
        );
    }
}
