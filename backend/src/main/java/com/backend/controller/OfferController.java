package com.backend.controller;

import com.backend.dto.CreateOfferRequest;
import com.backend.dto.OfferResponse;
import com.backend.entity.Client;
import com.backend.entity.Offer;
import com.backend.service.ClientService;
import com.backend.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;
    private final ClientService clientService;

    public OfferController(OfferService offerService, ClientService clientService) {
        this.offerService = offerService;
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<OfferResponse>> getAllOffers() {
        List<OfferResponse> responseList = offerService.getAllOffers();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Integer id) {
        Offer offer = offerService.getOfferById(id);
        return offer != null ? ResponseEntity.ok(offer) : ResponseEntity.notFound().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OfferResponse>> getOffersByClientId(@PathVariable Integer clientId) {
        List<OfferResponse> responseList = offerService.getOffersByClientId(clientId);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody CreateOfferRequest createOfferRequest) {
        // Fetch the client entity by ID
        Client client = clientService.getClientById(createOfferRequest.getClient());
        if (client == null) {
            return ResponseEntity.status(404).body(null); // Client not found
        }

        // Map the request DTO to the Offer entity
        Offer offer = Offer.builder()
                .client(client)
                .stockType(createOfferRequest.getStockType())
                .noOfStocks(createOfferRequest.getNoOfStocks())
                .pricePerStock(createOfferRequest.getPricePerStock())
                .offerType(createOfferRequest.getOfferType())
                .offerStatus(createOfferRequest.getOfferStatus())
                .build();

        // Save the offer
        Offer savedOffer = offerService.saveOffer(offer);

        // Map the saved offer to the response DTO
        OfferResponse response = OfferResponse.builder()
                .id(savedOffer.getId())
                .stockType(savedOffer.getStockType())
                .noOfStocks(savedOffer.getNoOfStocks())
                .pricePerStock(savedOffer.getPricePerStock())
                .offerType(savedOffer.getOfferType())
                .offerStatus(savedOffer.getOfferStatus())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Integer id, @RequestBody Offer offer) {
        Offer existingOffer = offerService.getOfferById(id);
        if (existingOffer != null) {
            offer.setId(id); // Ensure the ID remains consistent
            Offer updatedOffer = offerService.saveOffer(offer);
            return ResponseEntity.ok(updatedOffer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOffer(@PathVariable Integer id) {
        offerService.cancelOffer(id);
        return ResponseEntity.noContent().build();
    }
}
