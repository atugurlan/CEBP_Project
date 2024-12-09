package com.backend.service;

import com.backend.entity.Client;
import com.backend.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Integer id) {
        return clientRepository.findOneById(id);
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findOneByEmail(email);
    }

    public Client getClientByName(String name) {
        return clientRepository.findOneByName(name);
    }

    @Transactional
    public void updateMoneyWallet(Integer clientId, Integer newBalance) {
        Client client = getClientById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client not found for ID: " + clientId);
        }

        if (newBalance < 0) {
            throw new IllegalArgumentException("Money wallet balance cannot be negative.");
        }

        client.setMoneyWallet(newBalance);
        clientRepository.save(client);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClient(Integer id) {
        Client client = getClientById(id);
        if (client != null) {
            clientRepository.delete(client);
        } else {
            throw new IllegalArgumentException("Client not found for ID: " + id);
        }
    }
}
