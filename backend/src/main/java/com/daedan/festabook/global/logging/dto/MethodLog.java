package com.daedan.festabook.global.logging.dto;

public record MethodLog(
        String type,
        String className,
        String methodName,
        long executionTime
) {

    public static MethodLog from(String className, String methodName, long executionTime) {
        return new MethodLog(
                "Method",
                className,
                methodName,
                executionTime
        );
    }
}
