package com.backend.dto;

import com.backend.entity.StockType;
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
public class CreateStockWalletRequest {

    @NotNull(message = "Client ID is required.")
    private Integer client;

    @NotNull(message = "Stock type is required.")
    private StockType stockType;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
}
