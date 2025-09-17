package com.daedan.festabook.global.logging.dto;

public record MethodLog(
        LogType type,
        String className,
        String methodName,
        String executionTime
) {

    public static MethodLog from(String className, String methodName, String executionTime) {
        return new MethodLog(
                LogType.METHOD_EVENT,
                className,
                methodName,
                executionTime
        );
    }
}
