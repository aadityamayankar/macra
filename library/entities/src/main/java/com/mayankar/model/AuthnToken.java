package com.mayankar.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthnToken {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
}
