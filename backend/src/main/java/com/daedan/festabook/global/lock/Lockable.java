package com.daedan.festabook.global.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lockable {

    String spelKey();

    boolean useMethodScopeLock();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 5L;

    long leaseTime() default 3L;
}
