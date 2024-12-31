package com.mayankar.util;

import com.mayankar.dto.AuthCodeRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UrlConfig {
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    public static final String GOOGLE_REVOKE_URL = "https://accounts.google.com/o/oauth2/revoke";
    public static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static final String SELF_AUTHZ_URI_PATH = "/oauth2/v1/auth";
    public static final String SELF_TOKEN_URI_PATH = "/oauth2/v1/token";
    public static final String VER_V1 = "v1";

    public static String getGoogleAuthUrl() {
        return GOOGLE_AUTH_URL;
    }

    public static String getGoogleTokenUrl() {
        return GOOGLE_TOKEN_URL;
    }

    public static String getSelfTokenUrl(String authzBaseUrl) {
        return buildUriStringFromPath(authzBaseUrl, SELF_TOKEN_URI_PATH);
    }

    public static String getGoogleUserinfoUrl() {
        return GOOGLE_USERINFO_URL;
    }

    public static URI getSelfAuthzUri(AuthCodeRequest authCodeRequest, String authzBaseUrl) {
        return UriComponentsBuilder.fromUriString(buildUriStringFromPath(authzBaseUrl, SELF_AUTHZ_URI_PATH))
                .queryParam("response_type", authCodeRequest.getResponseType())
                .queryParam("client_id", authCodeRequest.getClientId())
                .queryParam("redirect_uri", authCodeRequest.getRedirectUri())
                .queryParam("scope", authCodeRequest.getScope())
                .queryParam("state", authCodeRequest.getState())
                .queryParam("user_id", authCodeRequest.getUserId())
                .build().toUri();
    }

    public static String buildUriStringFromPath(String baseUrl, String path) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .build().toUriString();
    }
}
