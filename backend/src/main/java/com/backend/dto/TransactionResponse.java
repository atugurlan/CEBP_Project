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
public class TransactionResponse {
    private Integer id;
    private Integer sellingClientId; // ID of the selling client
    private Integer buyingClientId;  // ID of the buying client
    private Integer sellingOfferId;  // ID of the selling offer
    private Integer buyingOfferId;   // ID of the buying offer
    private StockType tradedStockType;
    private Integer noOfTradedStocks;
    private Integer pricePerStock;
    private Integer totalPrice;
}
