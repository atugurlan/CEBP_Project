package com.backend.controller;

import com.backend.dto.TransactionResponse;
import com.backend.entity.Transaction;
import com.backend.entity.StockType;
import com.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
    }

    @GetMapping("/selling-client/{sellingClientId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBySellingClientId(@PathVariable Integer sellingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingClientId(sellingClientId));
    }

    @GetMapping("/buying-client/{buyingClientId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByBuyingClientId(@PathVariable Integer buyingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBuyingClientId(buyingClientId));
    }

    @GetMapping("/selling-client/{sellingClientId}/buying-client/{buyingClientId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBySellingAndBuyingClientId(
            @PathVariable Integer sellingClientId,
            @PathVariable Integer buyingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingAndBuyingClientId(sellingClientId, buyingClientId));
    }

    @GetMapping("/selling-offer/{sellingOfferId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBySellingOfferId(@PathVariable Integer sellingOfferId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingOfferId(sellingOfferId));
    }

    @GetMapping("/buying-offer/{buyingOfferId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByBuyingOfferId(@PathVariable Integer buyingOfferId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBuyingOfferId(buyingOfferId));
    }

    @GetMapping("/traded-stock-type/{tradedStockType}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByTradedStockType(@PathVariable StockType tradedStockType) {
        return ResponseEntity.ok(transactionService.getTransactionsByTradedStockType(tradedStockType));
    }
}

