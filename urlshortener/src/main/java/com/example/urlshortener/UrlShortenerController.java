package com.example.urlshortener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rebrandly.api.key}")
    private String apiKey;

    public UrlShortenerController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        String url = "https://api.rebrandly.com/v1/links";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create JSON payload
        String payload = "{ \"destination\": \"" + longUrl + "\", \"domain\": { \"fullName\": \"rebrand.ly\" } }";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            // Process the response
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String shortUrl = jsonResponse.path("shortUrl").asText();
            return ResponseEntity.ok("Shortened URL: " + shortUrl);
        } catch (HttpClientErrorException e) {
            // Handle error response
            String errorResponse = e.getResponseBodyAsString();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException | RestClientException e) {
            // Handle general exceptions
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
