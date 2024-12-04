package com.backend.controller;

import com.backend.dto.LoginRequest;
import com.backend.dto.RegisterClientRequest;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        Client client = clientService.getClientByEmail(loginRequest.getEmail());
        if (client != null && client.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok("Login successful");
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
