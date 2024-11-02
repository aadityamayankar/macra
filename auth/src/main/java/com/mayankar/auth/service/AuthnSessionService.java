package com.mayankar.auth.service;

import com.mayankar.model.AuthnSession;
import com.mayankar.model.AuthnToken;
import com.mayankar.model.CompositeID;
import com.mayankar.model.UserProfile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthnSessionService {
    public Mono<AuthnSession> createAuthnSession(UserProfile userProfile, AuthnToken authnToken) {
        AuthnSession authnSession = AuthnSession.builder()
                .id(UUID.randomUUID().toString())
                .userId(CompositeID.parseId(userProfile.getId()))
                .accessToken(authnToken.getAccessToken())
                .refreshToken(authnToken.getRefreshToken())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1800)) // @TODO: Set this configurable
                .build();
        return Mono.just(authnSession);
    }
}