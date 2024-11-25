package com.backend.repository;

import com.backend.entity.StockType;
import com.backend.entity.StockWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockWalletRepository extends JpaRepository<StockWallet, Integer> {
    StockWallet findOneById(Integer id);
    List<StockWallet> findOneByStockType(StockType stockType);
    StockWallet findOneByClientId(Integer clientId);
}
