package com.mayankar.authn.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsGIAM {
    private String id;
    private String email;
    private String name;
    private String picture;
    private Boolean verifiedEmail;
    private String familyName;
    private String givenName;
}
