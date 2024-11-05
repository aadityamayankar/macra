package com.mayankar.authz.config;

import com.mayankar.util.ConfigProps;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    @Autowired
    ConfigProps configProps;

    public String generateAccessToken(String userId, String scope) {
        return Jwts.builder()
                .subject(userId)
                .claim("scope", scope)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + configProps.getAuthnSessionDuration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateIdToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("authn_time", new Date())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + configProps.getAuthnSessionDuration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + configProps.getAuthzRefreshDuration()))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = configProps.authzCodeSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
