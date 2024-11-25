package com.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name="stock_id", nullable = false)
    private Stock stock;

    private int noOfStocks;
    private int noOfStocksLeft;
    private int pricePerStock;
    private OfferType offerType;
    private OfferStatus offerStatus;
}
