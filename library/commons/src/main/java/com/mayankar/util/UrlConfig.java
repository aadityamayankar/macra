package com.mayankar.util;

public class UrlConfig {
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    public static final String GOOGLE_REVOKE_URL = "https://accounts.google.com/o/oauth2/revoke";
    public static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    public static String getGoogleAuthUrl() {
        return GOOGLE_AUTH_URL;
    }

    public static String getGoogleTokenUrl() {
        return GOOGLE_TOKEN_URL;
    }

    public static String getGoogleUserinfoUrl() {
        return GOOGLE_USERINFO_URL;
    }
}
