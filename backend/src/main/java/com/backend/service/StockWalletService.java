package com.backend.service;

import com.backend.entity.StockType;
import com.backend.entity.StockWallet;
import com.backend.repository.StockWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockWalletService {

    private final StockWalletRepository stockWalletRepository;

    public StockWalletService(StockWalletRepository stockWalletRepository) {
        this.stockWalletRepository = stockWalletRepository;
    }

    public List<StockWallet> getAllStockWallets() {
        return stockWalletRepository.findAll();
    }

    public StockWallet getStockWalletById(Integer id) {
        return stockWalletRepository.findById(id).orElse(null);
    }

    public List<StockWallet> getStockWalletsByStockType(StockType stockType) {
        return stockWalletRepository.findOneByStockType(stockType);
    }

    public StockWallet getStockWalletByClientId(Integer clientId) {
        return stockWalletRepository.findOneByClientId(clientId);
    }

    @Transactional
    public StockWallet saveStockWallet(StockWallet stockWallet) {
        return stockWalletRepository.save(stockWallet);
    }

    @Transactional
    public void updateStockWallet(Integer clientId, StockType stockType, Integer quantityChange) {
        StockWallet stockWallet = getStockWalletByClientId(clientId);
        if (stockWallet == null) {
            throw new IllegalArgumentException("Stock wallet not found for client ID: " + clientId);
        }

        if (stockWallet.getStockType() != stockType) {
            throw new IllegalArgumentException("Mismatch in stock type for client ID: " + clientId);
        }

        int newQuantity = stockWallet.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stocks in wallet for client ID: " + clientId);
        }

        stockWallet.setQuantity(newQuantity);
        stockWalletRepository.save(stockWallet);
    }

    @Transactional
    public void deleteStockWallet(Integer id) {
        stockWalletRepository.deleteById(id);
    }
}
