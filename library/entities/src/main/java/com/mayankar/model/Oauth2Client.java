package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("oauth2_client")
public class Oauth2Client extends BaseEntityProfile {
    @Id
    private String id;
    private String clientId;
    private String clientSecret;
    private List<String> redirectUris;
    private List<String> scopes;
}
