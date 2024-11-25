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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="selling_client_id", nullable = false)
    private Client sellingClient;

    @ManyToOne
    @JoinColumn(name="buying_client_id", nullable = false)
    private Client buyingClient;

    @ManyToOne
    @JoinColumn(name="selling_offer_id", nullable = false)
    private Offer sellingOffer;

    @ManyToOne
    @JoinColumn(name="buying_offer_id", nullable = false)
    private Offer buyingOffer;

    private StockType tradedStockType;
    private Integer noOfTradedStocks;
    private Integer pricePerStock;
    private Integer totalPrice;
}
