package com.mayankar.authn.service;

import com.mayankar.model.AuthnToken;
import com.mayankar.util.ConfigProps;
import com.mayankar.util.ApiClient;
import com.mayankar.util.UrlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.*;

@Service
public class OAuth2Service {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);
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

        //@TODO: can create too many logs change it to debug
        logger.info("exchangeCodeForTokenGIAM: code={}, clientId={}, clientSecret={}, redirectUri={}", code, configProps.getGIAMClientId(), configProps.getGIAMClientSecret(), configProps.getGIAMRedirectUri());
        return apiClient.post(UrlConfig.getGoogleTokenUrl(), formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }

    public Mono<AuthnToken> exchangeCodeForTokenSelf(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CODE, code);
        formData.add(CLIENT_ID, configProps.getSelfClientId());
        formData.add(CLIENT_SECRET, configProps.getSelfClientSecret());
        formData.add(REDIRECT_URI, configProps.getSelfRedirectUri());
        formData.add(GRANT_TYPE, AUTHORIZATION_CODE);

        return apiClient.post(UrlConfig.getSelfTokenUrl(configProps.getAuthzBaseUrl()), formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }
}