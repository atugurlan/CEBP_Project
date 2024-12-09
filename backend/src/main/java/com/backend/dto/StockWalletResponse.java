package com.backend.dto;

import com.backend.entity.StockType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockWalletResponse {
    private StockType stockType;
    private Integer quantity;
}
