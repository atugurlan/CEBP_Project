package com.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class CommunicationController {

    private final RestTemplate restTemplate;

    public CommunicationController() {
        this.restTemplate = new RestTemplate();
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
        // validate offer
        String targetUrl = "http://localhost:8081/postOffer";
        /// offer is good
        ///
        ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, requestBody, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        ///
        /// else
        /// return response containing error (not enough stocks/ not enough money depending on offer type)
    }

}
