package org.nico.cache;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class Cache {

    private static RedisTemplate<String,Object> redisTemplate;

    private static RedisAtomicLong serialAtomicLong;

    private static RedisScript<Boolean> atomicLimitScript;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        Cache.redisTemplate = redisTemplate;
        Cache.serialAtomicLong = new RedisAtomicLong(KeyUtils.serialKey(), redisTemplate.getConnectionFactory());
    }

    @Autowired
    public void setAtomicLimitScript(RedisScript<Boolean> atomicLimitScript) {
        Cache.atomicLimitScript = atomicLimitScript;
    }

    public static abstract class KeyUtils {
        private static final String PROJECT_PREFIX = "RAY:";

        private static final String SEP = ":";

        private static final String UID = createKey("uid:");

        private static final String TOKEN = createKey("token:");

        private static final String GROUP = createKey("group:");

        private static final String SERIAL = createKey("serial");

        public static final String WAITING = createKey("waiting");

        public static final String WORKING = createKey("working");

        private static String createKey(String key) {
            return PROJECT_PREFIX + key;
        }

        public static String tokenKey(String token) {
            return TOKEN + token;
        }

        public static String groupKey(String uid) {
            return UID + uid + SEP + GROUP;
        }

        public static String serialKey() {
            return SERIAL;
        }

    }
    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOURS = 60 * MINUTE;

//    public static String tokenToUid(String token) {
//        return getOrLoad(tokenKey(token), k -> UserClient.connect(true).getUserId(token), 5 * SECOND);
//    }
//
//    public static Optional<String> group(String uid) {
//        return Optional.ofNullable((String) redisTemplate.opsForValue().get(groupKey(uid)));
//    }
//
//    public static void setGroup(String uid,String group) {
//        redisTemplate.opsForValue().set(groupKey(uid),group, YmlConfig.getInt("maxConnectedMinute") * MINUTE, TimeUnit.MILLISECONDS);
//    }
//
//    public static void delGroup(String uid) {
//        redisTemplate.delete(groupKey(uid));
//    }

    public static <T> ZSetOperations<String,T> zSet() {
        return (ZSetOperations<String, T>) redisTemplate.opsForZSet();
    }

    public static long incrSerialBy(long step) {
        return serialAtomicLong.getAndAdd(step);
    }

    public static boolean hasKey(String key) {
        return redisTemplate.hasKey(KeyUtils.createKey(key));
    }

    /**
     *
     * @param key
     * @param limit
     * @param expiredTimeoutMills
     * @return
     */
    public static boolean rateLimit(String key,Integer limit,Long expiredTimeoutMills) {
        return rateLimit(key,limit,expiredTimeoutMills,true /** if exception downgrade **/);
    }

    public static boolean rateLimit(String key,Integer limit,Long expiredTimeoutMills,boolean ifExceptionDowngrade) {
        try {
            return redisTemplate.execute(atomicLimitScript, Lists.newArrayList(KeyUtils.createKey(key)),limit,expiredTimeoutMills);
        } catch (Exception e) {
            log.error("redis rateLimit exception",e);
        }
        return !ifExceptionDowngrade;
    }

    public static Boolean remove(String key) {
        return redisTemplate.delete(KeyUtils.createKey(key));
    }


    private static <T> T getOrLoad(String key, Function<String,T> load, long expiredTimeoutMills) {
        final ValueOperations<String, T> valueOperations = (ValueOperations<String, T>) redisTemplate.opsForValue();
        T ret = valueOperations.get(key);
        if (!Objects.isNull(ret)) {
            return ret;
        }
        ret = load.apply(key);
        valueOperations.set(key, ret,expiredTimeoutMills, TimeUnit.MILLISECONDS);
        return ret;
    }
}
