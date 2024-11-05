package com.mayankar.authz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthzTokenRequest {
    private String grant_type;
    private String code;
    private String redirect_uri;
    private String client_id;
    private String client_secret;
}
