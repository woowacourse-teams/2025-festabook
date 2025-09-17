package com.daedan.festabook.global.logging.dto;

public record MethodLog(
        LogType type,
        String className,
        String methodName,
        long executionTime
) {

    public static MethodLog from(String className, String methodName, long executionTime) {
        return new MethodLog(
                LogType.METHOD_EVENT,
                className,
                methodName,
                executionTime
        );
    }
}
