package com.backend.controller;

import com.backend.entity.StockWallet;
import com.backend.entity.StockType;
import com.backend.service.StockWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-wallets")
public class StockWalletController {

    private final StockWalletService stockWalletService;

    public StockWalletController(StockWalletService stockWalletService) {
        this.stockWalletService = stockWalletService;
    }

    @GetMapping
    public ResponseEntity<List<StockWallet>> getAllStockWallets() {
        return ResponseEntity.ok(stockWalletService.getAllStockWallets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockWallet> getStockWalletById(@PathVariable Integer id) {
        StockWallet stockWallet = stockWalletService.getStockWalletById(id);
        return stockWallet != null ? ResponseEntity.ok(stockWallet) : ResponseEntity.notFound().build();
    }

    @GetMapping("/stockType/{stockType}")
    public ResponseEntity<List<StockWallet>> getStockWalletsByStockType(@PathVariable StockType stockType) {
        return ResponseEntity.ok(stockWalletService.getStockWalletsByStockType(stockType));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<StockWallet> getStockWalletByClientId(@PathVariable Integer clientId) {
        StockWallet stockWallet = stockWalletService.getStockWalletByClientId(clientId);
        return stockWallet != null ? ResponseEntity.ok(stockWallet) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<StockWallet> createStockWallet(@RequestBody StockWallet stockWallet) {
        StockWallet savedStockWallet = stockWalletService.saveStockWallet(stockWallet);
        return ResponseEntity.ok(savedStockWallet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockWallet> updateStockWallet(@PathVariable Integer id, @RequestBody StockWallet stockWallet) {
        StockWallet existingStockWallet = stockWalletService.getStockWalletById(id);
        if (existingStockWallet != null) {
            stockWallet.setId(id); // Ensure the ID is preserved
            StockWallet updatedStockWallet = stockWalletService.saveStockWallet(stockWallet);
            return ResponseEntity.ok(updatedStockWallet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockWallet(@PathVariable Integer id) {
        StockWallet existingStockWallet = stockWalletService.getStockWalletById(id);
        if (existingStockWallet != null) {
            stockWalletService.deleteStockWallet(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
