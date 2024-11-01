package com.mayankar.auth.service;

import com.mayankar.model.AuthnToken;
import com.mayankar.util.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    @Autowired
    ApiClient apiClient;

    public Mono<AuthnToken> exchangeCodeForToken(String code) {
        String clientId = "996208298744-in3qbmgh9b9bh1d1ffofcia3ot29ra6o.apps.googleusercontent.com";
        String clientSecret = "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";
        String redirectUri = "http://localhost:7001/oauth/callback";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        return apiClient.post("https://oauth2.googleapis.com/token", formData, AuthnToken.class, MediaType.APPLICATION_FORM_URLENCODED);
    }
}