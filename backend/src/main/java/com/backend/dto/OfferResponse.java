package com.backend.dto;

import com.backend.entity.OfferStatus;
import com.backend.entity.OfferType;
import com.backend.entity.StockType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferResponse {

    private Integer id;
    private Integer clientId; // New field for the client's ID
    private StockType stockType;
    private Integer noOfStocks;
    private Integer pricePerStock;
    private OfferType offerType;
    private OfferStatus offerStatus;
}
