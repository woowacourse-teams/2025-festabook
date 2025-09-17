package com.daedan.festabook.global.logging.dto;

public record MethodEventLog(
        String type,
        String className,
        String methodName
) {
}
