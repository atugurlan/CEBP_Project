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
public class RegisterClientRequest {
    @NotNull
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @NotNull
    @Email(message = "Invalid email address.")
    private String email;

    @NotNull
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters.")
    private String password;

    private Integer moneyWallet;
}
