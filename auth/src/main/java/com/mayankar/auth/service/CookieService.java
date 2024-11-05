package com.mayankar.auth.service;

import com.mayankar.model.AuthnSession;
import com.mayankar.util.ConfigProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static com.mayankar.util.Constants.SESSION_ID;

@Service
public class CookieService {
    @Autowired
    ConfigProps configProps;

    public ResponseCookie createSessionCookie(AuthnSession authnSession) {
        return ResponseCookie.from(SESSION_ID, authnSession.getId())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(configProps.getAuthnSessionDuration())
                .build();
    }
}