package com.daedan.festabook.global.logging.dto;

public record MethodEventLog(
        LogType type,
        String className,
        String methodName
) {

    public static MethodEventLog from(String className, String methodName) {
        return new MethodEventLog(
                LogType.METHOD_EVENT,
                className,
                methodName
        );
    }
}
