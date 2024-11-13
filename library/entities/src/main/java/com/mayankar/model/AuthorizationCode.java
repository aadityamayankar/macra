package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("authz_code")
public class AuthorizationCode {
    @Id
    private String code;
    private String userId;
    private String clientId;
    private String redirectUri;
    private String scope;
    private Instant createdAt;
    private Instant expiresAt;
}
