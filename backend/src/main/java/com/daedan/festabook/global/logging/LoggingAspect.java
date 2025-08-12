package com.daedan.festabook.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("""
            execution(* com.daedan.festabook..*.*(..)) &&
            (
                within(@org.springframework.web.bind.annotation.RestController *) ||
                within(@org.springframework.stereotype.Service *) ||
                execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))
            )
            """
    )
    public Object allLayersLogging(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("[Method Call] | Class: {} | Method: {} | Args: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs()
        );

        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long executionTime = end - start;

            log.info("[Method End] | Class: {} | Method: {} | Execution Time: {}ms | Response: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime,
                    result
            );
        }

        return result;
    }
}
