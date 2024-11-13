package com.mayankar.opsadmin.config;

import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    AuthnSessionRepository authnSessionRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return authnSessionRepository.getSession((String)authentication.getCredentials())
                .filter(Objects::nonNull)
                .map(authnSession -> {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(authnSession.getUserId(), authentication.getCredentials());
                    authenticationToken.setDetails(authnSession);
                    return authenticationToken;
                });
    }
}
