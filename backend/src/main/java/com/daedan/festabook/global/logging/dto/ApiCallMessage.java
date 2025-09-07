package com.daedan.festabook.global.logging.dto;

public record ApiCallMessage(
        String type,
        String httpMethod,
        String queryString,
        String uri
) {

    public static ApiCallMessage from(
            String httpMethod,
            String queryString,
            String uri
    ) {
        return new ApiCallMessage(
                "API Call",
                httpMethod,
                queryString,
                uri
        );
    }
}
