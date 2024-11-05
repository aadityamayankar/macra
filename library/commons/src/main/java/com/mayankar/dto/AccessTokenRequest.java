package com.mayankar.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class AccessTokenRequest extends BaseAuthRequest {
    private String grantType;
    private String code;
}
