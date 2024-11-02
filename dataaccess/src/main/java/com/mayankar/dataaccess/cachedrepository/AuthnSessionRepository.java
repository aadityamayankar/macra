package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import com.mayankar.model.AuthnSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class AuthnSessionRepository {
    @Autowired
    ReactiveRedisService<AuthnSession> reactiveRedisService;
    //@TODO: put the prefix as a constant
    public Mono<AuthnSession> save(AuthnSession authnSession) {
        return reactiveRedisService.save("authn_session", authnSession.getId(), authnSession);
    }

    public Mono<AuthnSession> getSession(String id) {
        return reactiveRedisService.get("authn_session", id, AuthnSession.class);
    }
}
