package com.backend.repository;

import com.backend.entity.Offer;
import com.backend.entity.OfferStatus;
import com.backend.entity.OfferType;
import com.backend.entity.StockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
    Offer findOneById(Integer id);
    List<Offer> findByClientId(Integer clientId);
    List<Offer> findByStockType(StockType stockType);
    List<Offer> findByOfferType(OfferType offerType);
    List<Offer> findByOfferStatus(OfferStatus offerStatus);
    List<Offer> findByOfferTypeAndOfferStatus(OfferType offerType, OfferStatus offerStatus);
}
