package com.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.backend.entity.Offer;
import com.backend.service.OfferService;
import com.backend.serializer.OfferDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CommunicationController {

    private final RestTemplate restTemplate;
    private final OfferService offerService;
    private final OfferDeserializer offerDeserializer;

    @Autowired
    public CommunicationController(OfferService offerService) {
        this.restTemplate = new RestTemplate();
        this.offerService = offerService;
        this.offerDeserializer = new OfferDeserializer();
    }

   /* @GetMapping("/redirect")
    public ResponseEntity<String> redirectRequest(@RequestParam String queryParam) {
        String targetUrl = "http://localhost:8081/";
        String fullUrl = targetUrl + "?queryParam=" + queryParam;


        ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }*/

    @PostMapping("/redirect")
    public ResponseEntity<String> redirectPostRequest(@RequestBody String requestBody) {
        System.out.println(requestBody);

        try {
            // Use ObjectMapper to create the JsonParser with a codec
            ObjectMapper objectMapper = new ObjectMapper();
            JsonFactory factory = objectMapper.getFactory(); // Use the factory from ObjectMapper
            JsonParser parser = factory.createParser(requestBody);
            parser.setCodec(objectMapper); // Set the codec to avoid NullPointerException

            Offer offer = offerDeserializer.deserialize(parser, null);

            // Validate offer
            int validationStatus = offerService.validateOffer(offer);

            switch (validationStatus) {
                case 0: // Offer is valid
                    String targetUrl = "http://localhost:8081/postOffer";
                    ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, requestBody, String.class);
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                case 1:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Client does not have enough stocks to sell.");
                case 2:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Client does not have enough money to buy.");
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Validation failed.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid request format.");
        }
    }
}
