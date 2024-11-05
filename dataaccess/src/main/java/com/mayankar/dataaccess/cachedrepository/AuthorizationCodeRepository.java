package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import com.mayankar.model.AuthorizationCode;
import com.mayankar.util.ConfigProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mayankar.util.CacheConstants.AUTHORIZATION_CODE_PREFIX;

@Repository
public class AuthorizationCodeRepository {
    @Autowired
    ReactiveRedisService<AuthorizationCode> reactiveRedisService;
    @Autowired
    private ConfigProps configProps;

    public Mono<AuthorizationCode> save(AuthorizationCode authorizationCode) {
        return reactiveRedisService.save(AUTHORIZATION_CODE_PREFIX, authorizationCode.getCode(), authorizationCode, configProps.getAuthzCodeDuration());
    }

    public Mono<AuthorizationCode> getAuthzCode(String code) {
        return reactiveRedisService.get(AUTHORIZATION_CODE_PREFIX, code, AuthorizationCode.class);
    }
}
