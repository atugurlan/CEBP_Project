package com.backend.dto;

import com.backend.entity.OfferStatus;
import com.backend.entity.OfferType;
import com.backend.entity.StockType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOfferRequest {

    @NotNull
    private Integer client; // Client ID

    private StockType stockType;

    private Integer noOfStocks;

    private Integer pricePerStock;

    private OfferType offerType;
}
