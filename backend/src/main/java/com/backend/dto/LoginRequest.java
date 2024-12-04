package com.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Email must not be null.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotNull(message = "Password must not be null.")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters.")
    private String password;
}
