package com.backend.service;

import com.backend.dto.OfferResponse;
import com.backend.entity.*;
import com.backend.repository.ClientRepository;
import com.backend.repository.OfferRepository;
import com.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OfferService {

    private final ClientRepository clientRepository;
    private final OfferRepository offerRepository;
    private final TransactionRepository transactionRepository;
    private final StockWalletService stockWalletService;
    private final ClientService clientService;

    public OfferService(ClientRepository clientRepository, OfferRepository offerRepository,
                        TransactionRepository transactionRepository,
                        StockWalletService stockWalletService,
                        ClientService clientService) {
        this.clientRepository = clientRepository;
        this.offerRepository = offerRepository;
        this.transactionRepository = transactionRepository;
        this.stockWalletService = stockWalletService;
        this.clientService = clientService;
    }

    public List<OfferResponse> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();

        // Map each Offer entity to the OfferResponse DTO
        return offers.stream()
                .map(offer -> OfferResponse.builder()
                        .id(offer.getId())
                        .clientId(offer.getClient().getId()) // Add clientId
                        .stockType(offer.getStockType())
                        .noOfStocks(offer.getNoOfStocks())
                        .pricePerStock(offer.getPricePerStock())
                        .offerType(offer.getOfferType())
                        .offerStatus(offer.getOfferStatus())
                        .build())
                .toList();
    }

    public Offer getOfferById(Integer id) {
        return offerRepository.findOneById(id);
    }

    @Transactional
    public Offer saveOffer(Offer offer, Client client) {
        // Validate the offer to ensure the client has the required resources
        int validationCode = validateOffer(offer);
        if (validationCode == 1) {
            throw new IllegalArgumentException("Client does not have enough stocks to sell.");
        } else if (validationCode == 2) {
            throw new IllegalArgumentException("Client does not have enough money to buy.");
        }

        // Set default status to PENDING if not provided
        if (offer.getOfferStatus() == null) {
            offer.setOfferStatus(OfferStatus.PENDING);
        }

        // Deduct resources based on the type of offer
        if (offer.getOfferStatus() == OfferStatus.PENDING) {
            if (offer.getOfferType() == OfferType.SELL) {
                // Deduct stocks from the client's stock wallet
                StockWallet stockWallet = stockWalletService.getStockWalletByClientIdAndStockType(
                        client.getId(),
                        offer.getStockType()
                );
                if (stockWallet == null || stockWallet.getQuantity() < offer.getNoOfStocks()) {
                    throw new IllegalArgumentException("Insufficient stocks in the client's stock wallet.");
                }
                stockWallet.setQuantity(stockWallet.getQuantity() - offer.getNoOfStocks());
                stockWalletService.saveStockWallet(stockWallet);
            } else if (offer.getOfferType() == OfferType.BUY) {
                // Deduct money from the client's money wallet
                int totalCost = offer.getNoOfStocks() * offer.getPricePerStock();
                if (client.getMoneyWallet() < totalCost) {
                    throw new IllegalArgumentException("Insufficient funds in the client's money wallet.");
                }
                client.setMoneyWallet(client.getMoneyWallet() - totalCost);
                clientService.saveClient(client);
            }
        }

        // Save the offer
        Offer savedOffer = offerRepository.save(offer);

        // Trigger matcher system after saving the new offer
        matchOffers(savedOffer);

        return savedOffer;
    }

    @Transactional
    public void cancelOffer(Integer id) {
        Offer offer = getOfferById(id);
        if (offer != null && offer.getOfferStatus() == OfferStatus.PENDING) {
            Client client = offer.getClient();

            if (offer.getOfferType() == OfferType.SELL) {
                // Restore stocks to the client's stock wallet
                StockWallet stockWallet = stockWalletService.getStockWalletByClientIdAndStockType(
                        client.getId(),
                        offer.getStockType()
                );
                if (stockWallet == null) {
                    // Create a new stock wallet if none exists
                    stockWallet = StockWallet.builder()
                            .client(client)
                            .stockType(offer.getStockType())
                            .quantity(offer.getNoOfStocks())
                            .build();
                } else {
                    stockWallet.setQuantity(stockWallet.getQuantity() + offer.getNoOfStocks());
                }
                stockWalletService.saveStockWallet(stockWallet);
            } else if (offer.getOfferType() == OfferType.BUY) {
                // Restore money to the client's money wallet
                int totalCost = offer.getNoOfStocks() * offer.getPricePerStock();
                client.setMoneyWallet(client.getMoneyWallet() + totalCost);
                clientService.saveClient(client);
            }

            // Mark the offer as CANCELLED
            offer.setOfferStatus(OfferStatus.CANCELLED);
            offerRepository.save(offer);
        }
    }

    public List<OfferResponse> getOffersByClientId(Integer clientId) {
        List<Offer> offers = offerRepository.findByClientId(clientId);

        // Map each Offer entity to OfferResponse
        return offers.stream()
                .map(offer -> OfferResponse.builder()
                        .id(offer.getId())
                        .clientId(offer.getClient().getId()) // Add clientId
                        .stockType(offer.getStockType())
                        .noOfStocks(offer.getNoOfStocks())
                        .pricePerStock(offer.getPricePerStock())
                        .offerType(offer.getOfferType())
                        .offerStatus(offer.getOfferStatus())
                        .build())
                .toList();
    }

    public int validateOffer(Offer offer) {
        Client client = clientRepository.findOneById(offer.getClient().getId());
        System.out.println(client);

        if (offer.getOfferType() == OfferType.SELL) {
            StockWallet stockWallet = stockWalletService.getStockWalletByClientIdAndStockType(offer.getClient().getId(), offer.getStockType());
            // Ensure client has enough stocks to sell
            if (stockWallet == null || stockWallet.getStockType() != offer.getStockType()
                    || stockWallet.getQuantity() < offer.getNoOfStocks()) {
                // throw new IllegalArgumentException("Client does not have enough stocks to sell.");
                return 1;
            }
        } else if (offer.getOfferType() == OfferType.BUY) {
            // Ensure client has enough money to buy
            int totalCost = offer.getNoOfStocks() * offer.getPricePerStock();
            if (client.getMoneyWallet() < totalCost) {
                // throw new IllegalArgumentException("Client does not have enough money to buy.");
                return 2;
            }
        }

        return 0;
    }

    @Transactional
    public void matchOffers(Offer newOffer) {
        List<Offer> potentialMatches = newOffer.getOfferType() == OfferType.SELL
                ? offerRepository.findByOfferTypeAndOfferStatus(OfferType.BUY, OfferStatus.PENDING)
                : offerRepository.findByOfferTypeAndOfferStatus(OfferType.SELL, OfferStatus.PENDING);

        for (Offer match : potentialMatches) {
            if (newOffer.getStockType() == match.getStockType()
                    && newOffer.getPricePerStock().equals(match.getPricePerStock())) {

                // Determine the number of stocks to trade
                int tradedStocks = Math.min(newOffer.getNoOfStocks(), match.getNoOfStocks());
                if (tradedStocks > 0) {
                    // Perform the transaction
                    performTransaction(newOffer, match, tradedStocks);

                    // Update the number of stocks left in the offers
                    newOffer.setNoOfStocks(newOffer.getNoOfStocks() - tradedStocks);
                    match.setNoOfStocks(match.getNoOfStocks() - tradedStocks);

                    // Mark offers as completed if fully fulfilled
                    if (newOffer.getNoOfStocks() == 0) {
                        newOffer.setOfferStatus(OfferStatus.COMPLETED);
                    }
                    if (match.getNoOfStocks() == 0) {
                        match.setOfferStatus(OfferStatus.COMPLETED);
                    }

                    offerRepository.save(newOffer);
                    offerRepository.save(match);

                    // If the new offer is fully completed, stop matching
                    if (newOffer.getOfferStatus() == OfferStatus.COMPLETED) {
                        break;
                    }
                }
            }
        }
    }

    // Perform a transaction without updating client resources
    private void performTransaction(Offer newOffer, Offer match, int tradedStocks) {
        Transaction transaction = Transaction.builder()
                .sellingClient(newOffer.getOfferType() == OfferType.SELL ? newOffer.getClient() : match.getClient())
                .buyingClient(newOffer.getOfferType() == OfferType.BUY ? newOffer.getClient() : match.getClient())
                .sellingOffer(newOffer.getOfferType() == OfferType.SELL ? newOffer : match)
                .buyingOffer(newOffer.getOfferType() == OfferType.BUY ? newOffer : match)
                .tradedStockType(newOffer.getStockType())
                .noOfTradedStocks(tradedStocks)
                .pricePerStock(newOffer.getPricePerStock())
                .totalPrice(tradedStocks * newOffer.getPricePerStock())
                .build();

        // Save the transaction to the database
        transactionRepository.save(transaction);
    }

}
