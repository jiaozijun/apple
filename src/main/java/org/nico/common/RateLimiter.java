package org.nico.common;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nico.cache.Cache;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Objects;

@Component
@Slf4j
@Aspect
public class RateLimiter {

    @Around("@annotation(redisRateLimit)")
    public Object rateLimit(ProceedingJoinPoint pjp, RedisRateLimit redisRateLimit) throws Throwable {

        final String rateLimitKey = rateLimitKey(pjp, redisRateLimit);
        log.info("redisRateLimit {} ; rateLimitKey {}",redisRateLimit,rateLimitKey);
        Throws.throwRateLimit(Cache.rateLimit(rateLimitKey, redisRateLimit.value(), redisRateLimit.per()));
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (redisRateLimit.once()) {
                Cache.remove(rateLimitKey);
            }
        }
    }


    private String rateLimitKey(ProceedingJoinPoint pjp,RedisRateLimit redisRateLimit) {
        String key = redisRateLimit.key();
        if (Strings.isNullOrEmpty(key)) {
            return pjp.getSignature().getDeclaringTypeName() + ":" + pjp.getSignature().getName();
        }
        Object[] args = pjp.getArgs();
        if (Objects.isNull(args) || args.length == 0) {
            return key;
        }
        return MessageFormat.format(key, args);

    }
}
