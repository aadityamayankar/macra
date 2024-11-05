package com.mayankar.auth.dto;

import com.mayankar.validation.PasswordCheck;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}