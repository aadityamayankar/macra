package com.mayankar.user.config;

import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    AuthnSessionRepository authnSessionRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return authnSessionRepository.getSession((String) authentication.getCredentials())
                .filter(Objects::nonNull)
                .flatMap(authnSession -> {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(authnSession.getUserId(), authentication.getCredentials());
                    authenticationToken.setDetails(authnSession);

                    return Mono.deferContextual(ctx -> {
                        ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
                        exchange.getAttributes().put("authnSession", authnSession);
                        return Mono.just(authenticationToken);
                    });
                });
    }
}
