package com.mayankar.authz.service;

import com.mayankar.authz.config.TokenProvider;
import com.mayankar.dto.AccessTokenRequest;
import com.mayankar.dataaccess.cachedrepository.AuthorizationCodeRepository;
import com.mayankar.model.AuthnToken;
import com.mayankar.util.ConfigProps;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.BEARER;

@Service
public class Oauth2AccessTokenService {
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final Oauth2ClientService oauth2ClientService;
    private final TokenProvider tokenProvider;
    private final ConfigProps configProps;

    public Oauth2AccessTokenService(AuthorizationCodeRepository authorizationCodeRepository, Oauth2ClientService oauth2ClientService, TokenProvider tokenProvider, ConfigProps configProps) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.oauth2ClientService = oauth2ClientService;
        this.tokenProvider = tokenProvider;
        this.configProps = configProps;
    }

    public Mono<AuthnToken> generateAccessToken(AccessTokenRequest accessTokenRequest) {
        return authorizationCodeRepository.getAuthzCode(accessTokenRequest.getCode())
                .filter(authorizationCode -> authorizationCode.getClientId().equals(accessTokenRequest.getClientId()) && authorizationCode.getRedirectUri().equals(accessTokenRequest.getRedirectUri()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid code")))
                .flatMap(authorizationCode -> oauth2ClientService.validateClient(accessTokenRequest, true)
                        .thenReturn(authorizationCode))
                .flatMap(authorizationCode -> {
                    final String accessToken = tokenProvider.generateAccessToken(authorizationCode.getUserId(), authorizationCode.getScope());
                    final String idToken = tokenProvider.generateIdToken(authorizationCode.getUserId());
                    final String refreshToken = tokenProvider.generateRefreshToken(authorizationCode.getUserId());
                    return Mono.just(AuthnToken.builder()
                            .accessToken(accessToken)
                            .idToken(idToken)
                            .refreshToken(refreshToken)
                            .tokenType(BEARER)
                            .expiresIn(configProps.getAuthnSessionDuration())
                            .scope(authorizationCode.getScope())
                            .build());
                });
    }
}