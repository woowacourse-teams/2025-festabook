package com.daedan.festabook.global.lock;

import com.daedan.festabook.global.logging.Loggable;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Loggable
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class LockAspect {

    private static final String DEFAULT_KEY_PREFIX_FORMAT = "%s::%s";

    private final LockStorage lockStorage;

    @Around("@annotation(com.daedan.festabook.global.lock.Lockable)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Lockable lockable = method.getAnnotation(Lockable.class);

        String keyPrefix = createKeyPrefix(lockable, signature);
        String parsedKey = keyPrefix + parseSpel(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                lockable.spelKey()
        );
        boolean isLockAcquired = false;
        try {
            lockStorage.tryLock(
                    parsedKey,
                    lockable.waitTime(),
                    lockable.leaseTime(),
                    lockable.timeUnit()
            );
            isLockAcquired = true;

            return joinPoint.proceed();
        } finally {
            if (isLockAcquired) {
                lockStorage.unlock(parsedKey);
            }
        }
    }

    public String parseSpel(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, String.class);
    }

    public String createKeyPrefix(Lockable lockable, MethodSignature signature) {
        if (lockable.usePrefix()) {
            String className = signature.getDeclaringType().getSimpleName();
            String methodName = signature.getName();

            return String.format(DEFAULT_KEY_PREFIX_FORMAT, className, methodName);
        }

        return "";
    }
}

