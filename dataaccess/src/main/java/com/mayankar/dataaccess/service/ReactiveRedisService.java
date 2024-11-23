package com.mayankar.dataaccess.service;

import com.mayankar.dataaccess.util.ObjectMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.time.Duration;

@Service
public class ReactiveRedisService <T> {
    @Autowired
    ReactiveRedisOperations<String, T> reactiveRedisOperations;

    private final Long DEFAULT_EXPIRY = 3600L;

    private String getKey(String prefix, String id) {
        return prefix + ":" + id;
    }

    public Mono<T> save(String prefix, String id, T value) {
        return reactiveRedisOperations.opsForValue().set(getKey(prefix, id), value, Duration.ofSeconds(DEFAULT_EXPIRY)).thenReturn(value);
    }

    public Mono<T> save(String prefix, String id, T value, Long expiry) {
        return reactiveRedisOperations.opsForValue().set(getKey(prefix, id), value, Duration.ofSeconds(expiry)).thenReturn(value);
    }

    public Mono<T> get(String prefix, String id, Class<T> type) {
        return reactiveRedisOperations.opsForValue().get(getKey(prefix, id))
                .map(val -> ObjectMapperUtil.objectMapper(val, type));
    }

    public Mono<Boolean> delete(String prefix, String id) {
        return reactiveRedisOperations.delete(getKey(prefix, id)).map(count -> count > 0);
    }

    public Mono<Boolean> exists(String prefix, String id) {
        return reactiveRedisOperations.hasKey(getKey(prefix, id));
    }

    public Flux<T> search(String prefix, String key, Object value, Class<T> type) {
        return reactiveRedisOperations.keys(prefix + "*")
                .flatMap(redisKey -> reactiveRedisOperations.opsForValue().get(redisKey)
                        .map(val -> ObjectMapperUtil.objectMapper(val, type)) // Deserialize the object
                        .filter(obj -> {
                            try {
                                // Use reflection to get the value of the specified field
                                Field field = type.getDeclaredField(key);
                                field.setAccessible(true); // Allow access to private fields
                                Object fieldValue = field.get(obj);
                                return fieldValue != null && fieldValue.equals(value);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                return false;
                            }
                        }))
                .switchIfEmpty(Flux.empty()); // Return an empty Flux if no matches are found
    }
}
