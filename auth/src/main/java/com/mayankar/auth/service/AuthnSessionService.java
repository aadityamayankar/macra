package com.mayankar.auth.service;

import com.mayankar.model.AuthnSession;
import com.mayankar.model.AuthnToken;
import com.mayankar.util.CompositeID;
import com.mayankar.model.UserProfile;
import com.mayankar.util.ConfigPropsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthnSessionService {
    @Autowired
    ConfigPropsService configPropsService;

    public Mono<AuthnSession> createAuthnSession(UserProfile userProfile, AuthnToken authnToken) {
        AuthnSession authnSession = AuthnSession.builder()
                .id(UUID.randomUUID().toString())
                .userId(CompositeID.parseId(userProfile.getId()))
                .accessToken(authnToken.getAccessToken())
                .refreshToken(authnToken.getRefreshToken())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(configPropsService.getAuthnSessionDuration()))
                .build();
        return Mono.just(authnSession);
    }
}