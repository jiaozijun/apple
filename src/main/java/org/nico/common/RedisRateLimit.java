package org.nico.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisRateLimit {
    /**
     * @return rate limit ${value} per ${per}ms
     */
    int value() default 1;

    /**
     *
     * @return key + {i} replace ith param , default key is pkg.class:method
     */
    String key() default "";


    /**
     *
     * @return per default 1000ms
     */
    long per() default 1000L;

    /**
     *
     * @return not rate limit，is once execute del
     */
    boolean once() default false;


}
