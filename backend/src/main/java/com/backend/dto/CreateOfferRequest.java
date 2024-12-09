package com.backend.dto;

import com.backend.entity.OfferStatus;
import com.backend.entity.OfferType;
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
public class CreateOfferRequest {

    @NotNull(message = "Client ID is required.")
    private Integer client;

    @NotNull(message = "Stock type is required.")
    private StockType stockType;

    @NotNull(message = "Number of stocks is required.")
    @Min(value = 1, message = "Number of stocks must be at least 1.")
    private Integer noOfStocks;

    @NotNull(message = "Price per stock is required.")
    @Min(value = 1, message = "Price per stock must be at least 1.")
    private Integer pricePerStock;

    @NotNull(message = "Offer type is required.")
    private OfferType offerType;

    @NotNull(message = "Offer status is required.")
    private OfferStatus offerStatus;
}
