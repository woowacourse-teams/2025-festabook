package com.daedan.festabook.global.logging.dto;

public record MethodEndMessage(
        String type,
        String className,
        String methodName,
        long executionTime
) {

    public static MethodEndMessage from(String className, String methodName, long executionTime) {
        return new MethodEndMessage(
                "Method End",
                className,
                methodName,
                executionTime
        );
    }
}
