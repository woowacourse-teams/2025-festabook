package com.daedan.festabook.global.logging.dto;

public record MethodCallMessage(
        String type,
        String className,
        String methodName
) {

    public static MethodCallMessage from(String className, String methodName) {
        return new MethodCallMessage(
                "Method Call",
                className,
                methodName
        );
    }
}
