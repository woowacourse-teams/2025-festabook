package com.daedan.festabook.global.logging.dto;

public record ApiEndMessage(
        String type,
        int httpStatusCode,
        Object requestBody,
        long executionTime
) {

    public static ApiEndMessage from(
            int httpStatusCode,
            Object requestBody,
            long executionTime
    ) {
        return new ApiEndMessage(
                "API End",
                httpStatusCode,
                requestBody,
                executionTime
        );
    }
}
