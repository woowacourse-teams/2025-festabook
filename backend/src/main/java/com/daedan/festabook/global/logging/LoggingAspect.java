package com.daedan.festabook.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *)")
    public Object allLayersLogging(ProceedingJoinPoint joinPoint) throws Throwable {

        // 메서드 정보
        log.info("[Method Call] | Class: {} | Method: {} | Args: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());

        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long executionTime = end - start;

            // 메서드 종료 시점 로그
            log.info("[Method End] | Class: {} | Method: {} | Execution Time: {}ms | Response: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime,
                    result);
        }

        return result;
    }
}