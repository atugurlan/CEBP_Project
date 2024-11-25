package com.backend.repository;

import com.backend.entity.Transaction;
import com.backend.entity.StockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySellingClientId(Integer sellingClientId);
    List<Transaction> findByBuyingClientId(Integer buyingClientId);
    List<Transaction> findBySellingClientIdAndBuyingClientId(Integer sellingClientId, Integer buyingClientId);
    List<Transaction> findBySellingOfferId(Integer sellingOfferId);
    List<Transaction> findByBuyingOfferId(Integer buyingOfferId);
    List<Transaction> findByTradedStockType(StockType tradedStockType);
    Transaction findOneById(Integer transactionId);
}
