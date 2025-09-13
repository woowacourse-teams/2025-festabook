package com.daedan.festabook.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Profile("!prod & !dev")
public class LocalLoggingAspect {

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
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[Method Call] className={} methodName={}", className, methodName);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long executionTime = end - start;
            
            log.info(
                    "[Method End] className={} methodName={} executionTime={}ms",
                    className,
                    methodName,
                    executionTime
            );
        }

        return result;
    }
}
