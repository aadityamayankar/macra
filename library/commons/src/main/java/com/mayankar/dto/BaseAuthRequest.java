package com.mayankar.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class BaseAuthRequest {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
