package com.mayankar.dataaccess.cachedrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;

import static com.mayankar.util.CacheConstants.*;

@Repository
public class TicketLockRepository {
    @Autowired
    ReactiveRedisOperations<String, String> reactiveRedisOperations;

    public Mono<Boolean> acquireLock(String key) {
        return reactiveRedisOperations.opsForValue().setIfAbsent(key, TICKET_LOCK_VAL, Duration.ofSeconds(TICKET_LOCK_EXPIRY))
                .map(acquired -> acquired != null && acquired)
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> releaseLock(String key) {
        return reactiveRedisOperations.delete(key).map(count -> count > 0);
    }

    public String generateLockKey(String eventId, String ticketId) {
        return MessageFormat.format(TICKET_LOCK_KEY, eventId, ticketId);
    }
}
