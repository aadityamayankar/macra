package com.mayankar.auth.service;

import com.mayankar.model.AuthnSession;
import com.mayankar.model.AuthnToken;
import com.mayankar.model.CompositeID;
import com.mayankar.model.UserProfile;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class CookieService {
    public ResponseCookie createSessionCookie(AuthnSession authnSession) {
        return ResponseCookie.from("session_id", authnSession.getId())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1800)
                .build();
    }
}