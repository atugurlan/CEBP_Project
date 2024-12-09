package com.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMoneyWalletRequest {

    @NotNull(message = "Money wallet value is required.")
    @Min(value = 0, message = "Money wallet value cannot be negative.")
    private Integer moneyWallet;
}
