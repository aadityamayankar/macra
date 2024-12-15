package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;

import static com.mayankar.util.CacheConstants.LAST_SYNC_TIME_KEY;

@Repository
public class LastSyncTimeRepository {
    @Autowired
    ReactiveRedisService<Instant> reactiveRedisService;

    public Mono<Instant> saveLastSyncTime(String entity, Instant lastSyncTime) {
        return reactiveRedisService.save(LAST_SYNC_TIME_KEY, entity, lastSyncTime);
    }

    public Mono<Instant> getLastSyncTime(String entity) {
        return reactiveRedisService.get(LAST_SYNC_TIME_KEY, entity, Instant.class).defaultIfEmpty(Instant.MIN);
    }
}
