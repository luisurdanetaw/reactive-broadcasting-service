package com.leuw.reactivebroadcastingservice.config;

import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisOperations<String, ChatMessage> reactiveRedisOperations(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, ChatMessage> serializationContext = RedisSerializationContext
                .<String, ChatMessage>newSerializationContext(new StringRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new Jackson2JsonRedisSerializer<>(ChatMessage.class))
                .build();
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}