package com.example.urlshortener;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UrlShorteningService {

    // In-memory storage for URLs
    private static final Map<String, String> urlMap = new HashMap<>();

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("url");
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("URL must not be empty");
        }
        
        // Generate a random UUID and use the first 6 characters as the shortened path
        String shortUrlPath = UUID.randomUUID().toString().substring(0, 6);
        urlMap.put(shortUrlPath, originalUrl);
        
        return "Shortened URL: http://localhost:8080/api/" + shortUrlPath;
    }

    @GetMapping("/{shortUrlPath}")
    public void redirectToOriginalUrl(@PathVariable String shortUrlPath, HttpServletResponse response) throws IOException {
        String originalUrl = urlMap.get(shortUrlPath);
        if (originalUrl == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found");
        } else {
            response.sendRedirect(originalUrl);
        }
    }
}
