package com.backend.service;

import com.backend.entity.Client;
import com.backend.entity.StockType;
import com.backend.entity.StockWallet;
import com.backend.repository.ClientRepository;
import com.backend.repository.StockWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockWalletService {

    private final StockWalletRepository stockWalletRepository;
    private final ClientRepository clientRepository;

    public StockWalletService(StockWalletRepository stockWalletRepository, ClientRepository clientRepository) {
        this.stockWalletRepository = stockWalletRepository;
        this.clientRepository = clientRepository;
    }

    public List<StockWallet> getAllStockWallets() {
        return stockWalletRepository.findAll();
    }

    public StockWallet getStockWalletById(Integer id) {
        return stockWalletRepository.findById(id).orElse(null);
    }

    public List<StockWallet> getStockWalletsByStockType(StockType stockType) {
        return stockWalletRepository.findAllByStockType(stockType);
    }

    public List<StockWallet> getStockWalletByClientId(Integer clientId) {
        return stockWalletRepository.findAllByClientId(clientId);
    }

    public StockWallet getStockWalletByClientIdAndStockType(Integer clientId, StockType stockType) {
        return stockWalletRepository.findOneByClientIdAndStockType(clientId, stockType);
    }

    @Transactional
    public StockWallet saveStockWallet(StockWallet stockWallet) {
        return stockWalletRepository.save(stockWallet);
    }

    @Transactional
    public void updateStockWallet(Integer clientId, StockType stockType, Integer quantityChange) {

        StockWallet findStockWallet = stockWalletRepository.findOneByClientIdAndStockType(clientId, stockType);
        Client client = clientRepository.findOneById(clientId);

        if (findStockWallet != null) {
            int newQuantity = findStockWallet.getQuantity() + quantityChange;
            findStockWallet.setQuantity(newQuantity);
        }
        else {
            findStockWallet = new StockWallet();
            findStockWallet.setStockType(stockType);
            findStockWallet.setQuantity(quantityChange);
            findStockWallet.setClient(client);
        }

        stockWalletRepository.save(findStockWallet);
    }

    @Transactional
    public void deleteStockWallet(Integer id) {
        stockWalletRepository.deleteById(id);
    }
}
