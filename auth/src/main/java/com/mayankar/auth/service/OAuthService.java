package com.mayankar.auth.service;

import com.mayankar.model.AuthnToken;
import com.mayankar.util.ConfigPropsService;
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
public class OAuthService {
    @Autowired
    ApiClient apiClient;
    @Autowired
    ConfigPropsService ConfigPropsService;

    public Mono<AuthnToken> exchangeCodeForTokenGIAM(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CODE, code);
        formData.add(CLIENT_ID, ConfigPropsService.getGIAMClientId());
        formData.add(CLIENT_SECRET, ConfigPropsService.getGIAMClientSecret());
        formData.add(REDIRECT_URI, ConfigPropsService.getGIAMRedirectUri());
        formData.add(GRANT_TYPE, AUTHORIZATION_CODE);

        return apiClient.post(UrlConfig.getGoogleTokenUrl(), formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }
}