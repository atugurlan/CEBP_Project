package com.backend.controller;

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
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
    }

    @GetMapping("/selling-client/{sellingClientId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySellingClientId(@PathVariable Integer sellingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingClientId(sellingClientId));
    }

    @GetMapping("/buying-client/{buyingClientId}")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyingClientId(@PathVariable Integer buyingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBuyingClientId(buyingClientId));
    }

    @GetMapping("/selling-client/{sellingClientId}/buying-client/{buyingClientId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySellingAndBuyingClientId(
            @PathVariable Integer sellingClientId,
            @PathVariable Integer buyingClientId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingAndBuyingClientId(sellingClientId, buyingClientId));
    }

    @GetMapping("/selling-offer/{sellingOfferId}")
    public ResponseEntity<List<Transaction>> getTransactionsBySellingOfferId(@PathVariable Integer sellingOfferId) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellingOfferId(sellingOfferId));
    }

    @GetMapping("/buying-offer/{buyingOfferId}")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyingOfferId(@PathVariable Integer buyingOfferId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBuyingOfferId(buyingOfferId));
    }

    @GetMapping("/traded-stock-type/{tradedStockType}")
    public ResponseEntity<List<Transaction>> getTransactionsByTradedStockType(@PathVariable StockType tradedStockType) {
        return ResponseEntity.ok(transactionService.getTransactionsByTradedStockType(tradedStockType));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody Transaction transaction) {
        Transaction existingTransaction = transactionService.getTransactionById(id);
        if (existingTransaction != null) {
            transaction.setId(id); // Ensure the ID is preserved
            Transaction updatedTransaction = transactionService.saveTransaction(transaction);
            return ResponseEntity.ok(updatedTransaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        Transaction existingTransaction = transactionService.getTransactionById(id);
        if (existingTransaction != null) {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
