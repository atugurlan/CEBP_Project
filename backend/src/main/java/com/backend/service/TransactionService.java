package com.backend.service;

import com.backend.entity.Transaction;
import com.backend.entity.StockType;
import com.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findOneById(id);
    }

    public List<Transaction> getTransactionsBySellingClientId(Integer sellingClientId) {
        return transactionRepository.findBySellingClientId(sellingClientId);
    }

    public List<Transaction> getTransactionsByBuyingClientId(Integer buyingClientId) {
        return transactionRepository.findByBuyingClientId(buyingClientId);
    }

    public List<Transaction> getTransactionsBySellingAndBuyingClientId(Integer sellingClientId, Integer buyingClientId) {
        return transactionRepository.findBySellingClientIdAndBuyingClientId(sellingClientId, buyingClientId);
    }

    public List<Transaction> getTransactionsBySellingOfferId(Integer sellingOfferId) {
        return transactionRepository.findBySellingOfferId(sellingOfferId);
    }

    public List<Transaction> getTransactionsByBuyingOfferId(Integer buyingOfferId) {
        return transactionRepository.findByBuyingOfferId(buyingOfferId);
    }

    public List<Transaction> getTransactionsByTradedStockType(StockType tradedStockType) {
        return transactionRepository.findByTradedStockType(tradedStockType);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
    }
}
