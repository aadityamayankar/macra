package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import com.mayankar.model.AuthnSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mayankar.util.CacheConstants.AUTHN_SESSION_PREFIX;

@Repository
public class AuthnSessionRepository {
    @Autowired
    ReactiveRedisService<AuthnSession> reactiveRedisService;
    public Mono<AuthnSession> save(AuthnSession authnSession) {
        return reactiveRedisService.save(AUTHN_SESSION_PREFIX, authnSession.getId(), authnSession);
    }

    public Mono<AuthnSession> getSession(String id) {
        return reactiveRedisService.get(AUTHN_SESSION_PREFIX, id, AuthnSession.class);
    }
}
