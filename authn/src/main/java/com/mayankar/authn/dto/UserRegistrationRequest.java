package com.mayankar.authn.dto;

import com.mayankar.validation.PasswordCheck;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    @NotNull
    private String email;
    @NotNull
    @PasswordCheck
    private String password;
}