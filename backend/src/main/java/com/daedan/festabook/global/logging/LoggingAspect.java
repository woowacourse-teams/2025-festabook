package com.daedan.festabook.global.logging;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.daedan.festabook.global.logging.dto.MethodEventLog;
import com.daedan.festabook.global.logging.dto.MethodLog;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class LoggingAspect {

    private final Tracer tracer;

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
        StopWatch stopWatch = new StopWatch();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        MethodEventLog methodEvent = MethodEventLog.from(className, methodName);
        log.info("", kv("event", methodEvent));

        Span span = tracer.spanBuilder(className + "::" + methodName).startSpan();

        Object result = null;
        stopWatch.start();
        try (Scope scope = span.makeCurrent()) {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();

            MethodLog methodLog = MethodLog.from(className, methodName, executionTime);
            log.info("", kv("event", methodLog));

            span.end();
        }

        return result;
    }
}
