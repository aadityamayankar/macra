package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("authn_session")
public class AuthnSession {
    @Id
    private String id;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant lastAccessedAt;
    private Integer role;
}
