package com.backend.service;

import com.backend.entity.*;
import com.backend.repository.OfferRepository;
import com.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final TransactionRepository transactionRepository;
    private final StockWalletService stockWalletService;
    private final ClientService clientService;

    public OfferService(OfferRepository offerRepository,
                        TransactionRepository transactionRepository,
                        StockWalletService stockWalletService,
                        ClientService clientService) {
        this.offerRepository = offerRepository;
        this.transactionRepository = transactionRepository;
        this.stockWalletService = stockWalletService;
        this.clientService = clientService;
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public Offer getOfferById(Integer id) {
        return offerRepository.findOneById(id);
    }

    @Transactional
    public Offer saveOffer(Offer offer) {
        validateOffer(offer); // Check client's resources before saving

        if (offer.getOfferStatus() == null) {
            offer.setOfferStatus(OfferStatus.PENDING); // Default to PENDING if not set
        }

        Offer savedOffer = offerRepository.save(offer);

        // Trigger matcher system after saving the new offer
        matchOffers(savedOffer);

        return savedOffer;
    }

    public void cancelOffer(Integer id) {
        Offer offer = getOfferById(id);
        if (offer != null) {
            offer.setOfferStatus(OfferStatus.CANCELLED);
            offerRepository.save(offer);
        }
    }

    private void validateOffer(Offer offer) {
        Client client = offer.getClient();
        StockWallet stockWallet = stockWalletService.getStockWalletByClientId(client.getId());

        if (offer.getOfferType() == OfferType.SELL) {
            // Ensure client has enough stocks to sell
            if (stockWallet == null || stockWallet.getStockType() != offer.getStockType()
                    || stockWallet.getQuantity() < offer.getNoOfStocks()) {
                throw new IllegalArgumentException("Client does not have enough stocks to sell.");
            }
        } else if (offer.getOfferType() == OfferType.BUY) {
            // Ensure client has enough money to buy
            int totalCost = offer.getNoOfStocks() * offer.getPricePerStock();
            if (client.getMoneyWallet() < totalCost) {
                throw new IllegalArgumentException("Client does not have enough money to buy.");
            }
        }
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
                int tradedStocks = Math.min(newOffer.getNoOfStocksLeft(), match.getNoOfStocksLeft());
                if (tradedStocks > 0) {
                    // Perform the transaction
                    performTransaction(newOffer, match, tradedStocks);

                    // Update the number of stocks left in the offers
                    newOffer.setNoOfStocksLeft(newOffer.getNoOfStocksLeft() - tradedStocks);
                    match.setNoOfStocksLeft(match.getNoOfStocksLeft() - tradedStocks);

                    // Mark offers as completed if fully fulfilled
                    if (newOffer.getNoOfStocksLeft() == 0) {
                        newOffer.setOfferStatus(OfferStatus.COMPLETED);
                    }
                    if (match.getNoOfStocksLeft() == 0) {
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

    // Perform a transaction and update client resources
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

        transactionRepository.save(transaction);

        // Update money wallets
        Client sellingClient = transaction.getSellingClient();
        Client buyingClient = transaction.getBuyingClient();
        int totalPrice = transaction.getTotalPrice();

        clientService.updateMoneyWallet(sellingClient.getId(), sellingClient.getMoneyWallet() + totalPrice);
        clientService.updateMoneyWallet(buyingClient.getId(), buyingClient.getMoneyWallet() - totalPrice);

        // Update stock wallets
        stockWalletService.updateStockWallet(sellingClient.getId(), transaction.getTradedStockType(), -tradedStocks);
        stockWalletService.updateStockWallet(buyingClient.getId(), transaction.getTradedStockType(), tradedStocks);
    }
}
