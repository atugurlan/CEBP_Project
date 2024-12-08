package com.backend.controller;

import com.backend.dto.CreateStockWalletRequest;
import com.backend.dto.StockWalletResponse;
import com.backend.entity.Client;
import com.backend.entity.StockWallet;
import com.backend.entity.StockType;
import com.backend.service.ClientService;
import com.backend.service.StockWalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-wallets")
public class StockWalletController {

    private final StockWalletService stockWalletService;
    private final ClientService clientService;

    public StockWalletController(StockWalletService stockWalletService, ClientService clientService) {
        this.stockWalletService = stockWalletService;
        this.clientService = clientService;
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

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<StockWalletResponse>> getStockWalletByClientId(@PathVariable Integer clientId) {
        List<StockWallet> stockWallets = stockWalletService.getStockWalletByClientId(clientId);

        // If no stock wallets are found, return an empty list
        if (stockWallets == null || stockWallets.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Return an empty list
        }

        // Map StockWallet entities to StockWalletResponse DTOs
        List<StockWalletResponse> responses = stockWallets.stream()
                .map(wallet -> StockWalletResponse.builder()
                        .stockType(wallet.getStockType())
                        .quantity(wallet.getQuantity())
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<StockWallet> createStockWallet(@Valid @RequestBody CreateStockWalletRequest stockWalletRequest) {
        // Fetch the client entity by ID
        Client client = clientService.getClientById(stockWalletRequest.getClient());
        if (client == null) {
            return ResponseEntity.status(404).body(null); // Client not found
        }

        // Map the DTO to the StockWallet entity
        StockWallet stockWallet = StockWallet.builder()
                .client(client) // Use the managed Client entity
                .stockType(stockWalletRequest.getStockType())
                .quantity(stockWalletRequest.getQuantity())
                .build();

        // Save the StockWallet entity
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
