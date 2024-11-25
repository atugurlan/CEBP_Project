package com.backend.controller;

import com.backend.entity.Offer;
import com.backend.service.OfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Integer id) {
        Offer offer = offerService.getOfferById(id);
        return offer != null ? ResponseEntity.ok(offer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
        Offer savedOffer = offerService.saveOffer(offer);
        return ResponseEntity.ok(savedOffer);
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
