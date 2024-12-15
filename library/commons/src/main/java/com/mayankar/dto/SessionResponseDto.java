package com.mayankar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseDto {
    private Boolean isAuthenticated = false;
}
