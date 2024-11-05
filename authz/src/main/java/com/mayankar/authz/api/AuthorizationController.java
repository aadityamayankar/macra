package com.mayankar.authz.api;

import com.mayankar.authz.dto.AuthzTokenRequest;
import com.mayankar.dto.AccessTokenRequest;
import com.mayankar.dto.AuthCodeRequest;
import com.mayankar.authz.service.AuthorizationService;
import com.mayankar.authz.service.Oauth2AccessTokenService;
import com.mayankar.model.AuthnToken;
import com.mayankar.util.UrlConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.*;

@RestController
@RequestMapping("/oauth2/" + UrlConfig.VER_V1)
public class AuthorizationController {
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    private Oauth2AccessTokenService oauth2AccessTokenService;

    @GetMapping("/auth")
    public Mono<ResponseEntity<Void>> authorize(
            @RequestParam("response_type") String responseType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("scope") String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam("user_id") String userId
    ) {
        if (!CODE.equalsIgnoreCase(responseType)) {
            return Mono.error(new IllegalArgumentException("Invalid response_type"));
        }
        AuthCodeRequest authorizeCodeRequest = AuthCodeRequest.builder()
                .responseType(responseType)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .state(state)
                .userId(userId)
                .build();
        return authorizationService.authorize(authorizeCodeRequest)
                .map(code -> authorizationService.buildRedirectUri(redirectUri, code, scope, state, userId))
                .map(uri -> ResponseEntity.status(HttpStatus.FOUND).location(java.net.URI.create(uri)).build());
    }

    @PostMapping(value = "/token", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public Mono<AuthnToken> token(
            @ModelAttribute AuthzTokenRequest authzTokenRequest
    ) {
        String grantType = authzTokenRequest.getGrant_type();
        String code = authzTokenRequest.getCode();
        String redirectUri = authzTokenRequest.getRedirect_uri();
        String clientId = authzTokenRequest.getClient_id();
        String clientSecret = authzTokenRequest.getClient_secret();

        if (!AUTHORIZATION_CODE.equalsIgnoreCase(grantType)) {
            return Mono.error(new IllegalArgumentException("Invalid grant_type"));
        }
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .grantType(grantType)
                .code(code)
                .redirectUri(redirectUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        return oauth2AccessTokenService.generateAccessToken(accessTokenRequest);
    }

    //@TODO: refresh token
}
