package com.backend.controller;

import com.backend.dto.LoginRequest;
import com.backend.dto.LoginResponse;
import com.backend.dto.MoneyWalletResponse;
import com.backend.dto.RegisterClientRequest;
import com.backend.dto.UpdateMoneyWalletRequest;
import com.backend.entity.Client;
import com.backend.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Integer id) {
        Client client = clientService.getClientById(id);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Client client = clientService.getClientByEmail(email);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        Client client = clientService.getClientByName(name);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody RegisterClientRequest registerRequest) {
        // Map DTO to the Client entity
        Client client = Client.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .moneyWallet(registerRequest.getMoneyWallet())
                .build();

        // Save the client
        Client savedClient = clientService.saveClient(client);
        return ResponseEntity.ok(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer id, @RequestBody Client client) {
        Client existingClient = clientService.getClientById(id);
        if (existingClient != null) {
            client.setId(id); // Ensure the ID is preserved
            Client updatedClient = clientService.saveClient(client);
            return ResponseEntity.ok(updatedClient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/money-wallet")
    public ResponseEntity<MoneyWalletResponse> getMoneyWallet(@PathVariable Integer id) {
        // Fetch the client
        Client client = clientService.getClientById(id);
        if (client != null) {
            // Map the moneyWallet to the response DTO
            MoneyWalletResponse response = MoneyWalletResponse.builder()
                    .moneyWallet(client.getMoneyWallet())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/money-wallet")
    public ResponseEntity<Client> updateMoneyWallet(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMoneyWalletRequest request) {

        // Fetch the existing client
        Client existingClient = clientService.getClientById(id);
        if (existingClient != null) {
            // Update the moneyWallet field
            existingClient.setMoneyWallet(request.getMoneyWallet());

            // Save the updated client
            Client updatedClient = clientService.saveClient(existingClient);
            return ResponseEntity.ok(updatedClient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Client client = clientService.getClientByEmail(loginRequest.getEmail());
        if (client != null && client.getPassword().equals(loginRequest.getPassword())) {
            // Create a response containing the user details
            LoginResponse loginResponse = LoginResponse.builder()
                    .id(client.getId())
                    .email(client.getEmail())
                    .password(client.getPassword())
                    .build();

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
        Client existingClient = clientService.getClientById(id);
        if (existingClient != null) {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
