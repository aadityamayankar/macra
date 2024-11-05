package com.mayankar.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class AuthCodeRequest extends BaseAuthRequest {
    private String responseType;
    private String redirectUri;
    private String scope; //This is a space-delimited list of strings
    private String state;
    private String userId;
}
