package com.mayankar.authz.service;

import com.mayankar.dto.AuthCodeRequest;
import com.mayankar.dataaccess.cachedrepository.AuthorizationCodeRepository;
import com.mayankar.model.AuthorizationCode;
import com.mayankar.util.ConfigProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AuthorizationService {
    @Autowired
    Oauth2ClientService oauth2ClientService;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private ConfigProps configProps;

    public Mono<String> authorize(AuthCodeRequest authorizeCodeRequest) {
        return oauth2ClientService.validateClient(authorizeCodeRequest, false)
                .flatMap(oauth2Client -> generateAuthorizationCode(authorizeCodeRequest));
    }

    private Mono<String> generateAuthorizationCode(AuthCodeRequest authorizeCodeRequest) {
        String authzCode = UUID.randomUUID().toString();
        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .code(authzCode)
                .clientId(authorizeCodeRequest.getClientId())
                .redirectUri(authorizeCodeRequest.getRedirectUri())
                .scope(authorizeCodeRequest.getScope())
                .userId(authorizeCodeRequest.getUserId())
                .createdAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(configProps.getAuthzCodeDuration()))
                .build();
        return authorizationCodeRepository.save(authorizationCode)
                .map(AuthorizationCode::getCode);
    }

    public String buildRedirectUri(String redirectUri, String code, String scope, String state, String userId) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", code)
                .queryParam("scope", scope)
                .queryParam("user_id", userId);
        if (state != null) {
            uriComponentsBuilder.queryParam("state", state);
        }
        return uriComponentsBuilder.toUriString();
    }
}
