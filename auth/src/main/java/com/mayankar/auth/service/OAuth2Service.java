package com.mayankar.auth.service;

import com.mayankar.model.AuthnToken;
import com.mayankar.util.ConfigProps;
import com.mayankar.util.ApiClient;
import com.mayankar.util.UrlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.*;

@Service
public class OAuth2Service {
    @Autowired
    ApiClient apiClient;
    @Autowired
    ConfigProps configProps;

    public Mono<AuthnToken> exchangeCodeForTokenGIAM(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CODE, code);
        formData.add(CLIENT_ID, configProps.getGIAMClientId());
        formData.add(CLIENT_SECRET, configProps.getGIAMClientSecret());
        formData.add(REDIRECT_URI, configProps.getGIAMRedirectUri());
        formData.add(GRANT_TYPE, AUTHORIZATION_CODE);

        return apiClient.post(UrlConfig.getGoogleTokenUrl(), formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }

    public Mono<AuthnToken> exchangeCodeForTokenSelf(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CODE, code);
        formData.add(CLIENT_ID, configProps.getSelfClientId());
        formData.add(CLIENT_SECRET, configProps.getSelfClientSecret());
        formData.add(REDIRECT_URI, configProps.getSelfRedirectUri());
        formData.add(GRANT_TYPE, AUTHORIZATION_CODE);

        return apiClient.post(UrlConfig.getSelfTokenUrl(), formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }
}