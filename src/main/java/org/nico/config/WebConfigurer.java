package org.nico.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
@Slf4j
public class WebConfigurer implements WebMvcConfigurer {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

//        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
//        redisTemplate.setDefaultSerializer(fastJsonRedisSerializer);

        redisTemplate.setKeySerializer(new StringRedisSerializer());//custom keySerializer
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//custom valueSerializer
        return redisTemplate;
    }


    @Bean
    public RedisScript<Boolean> atomicLimitScript() {
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/atomic_limit.lua"));
        try {
            return RedisScript.of(scriptSource.getScriptAsString(), Boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("load lua file error",e);
            throw new RuntimeException(e);
        }
    }
}
