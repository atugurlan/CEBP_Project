package com.backend.service;

import com.backend.dto.TransactionResponse;
import com.backend.entity.StockType;
import com.backend.entity.Transaction;
import com.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findOneById(id);
    }

    public List<TransactionResponse> getTransactionsBySellingClientId(Integer sellingClientId) {
        return transactionRepository.findBySellingClientId(sellingClientId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByBuyingClientId(Integer buyingClientId) {
        return transactionRepository.findByBuyingClientId(buyingClientId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsBySellingAndBuyingClientId(Integer sellingClientId, Integer buyingClientId) {
        return transactionRepository.findBySellingClientIdAndBuyingClientId(sellingClientId, buyingClientId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsBySellingOfferId(Integer sellingOfferId) {
        return transactionRepository.findBySellingOfferId(sellingOfferId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByBuyingOfferId(Integer buyingOfferId) {
        return transactionRepository.findByBuyingOfferId(buyingOfferId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByTradedStockType(StockType tradedStockType) {
        return transactionRepository.findByTradedStockType(tradedStockType).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
    }

    // Helper method to map Transaction entity to TransactionResponse DTO
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .sellingClientId(transaction.getSellingClient().getId())
                .buyingClientId(transaction.getBuyingClient().getId())
                .sellingOfferId(transaction.getSellingOffer().getId())
                .buyingOfferId(transaction.getBuyingOffer().getId())
                .tradedStockType(transaction.getTradedStockType())
                .noOfTradedStocks(transaction.getNoOfTradedStocks())
                .pricePerStock(transaction.getPricePerStock())
                .totalPrice(transaction.getTotalPrice())
                .build();
    }
}
