package com.mayankar.auth.service;

import com.mayankar.dto.UserProfileWithDetails;
import com.mayankar.model.AuthnSession;
import com.mayankar.model.AuthnToken;
import com.mayankar.util.CompositeID;
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

    public Mono<AuthnSession> createAuthnSession(UserProfileWithDetails userProfileWithDetails, AuthnToken authnToken) {
        AuthnSession authnSession = AuthnSession.builder()
                .id(UUID.randomUUID().toString())
                .userId(CompositeID.parseId(userProfileWithDetails.getId()))
                .accessToken(authnToken.getAccessToken())
                .refreshToken(authnToken.getRefreshToken())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(configPropsService.getAuthnSessionDuration()))
                .lastAccessedAt(Instant.now())
                .role(userProfileWithDetails.getRole().getValue())
                .build();
        return Mono.just(authnSession);
    }
}