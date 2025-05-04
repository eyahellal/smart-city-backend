package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/geo")
public class GeoController {
    @Autowired
    private GeoService geoService;

    @GetMapping("/search")
    public ResponseEntity<String> searchLocation(@RequestParam String query) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "SmartCityApp/1.0") // Nominatim requires this
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/reverse")
    public ResponseEntity<String> reverseGeocode(@RequestParam double lat, @RequestParam double lon) {
        String result = geoService.reverseGeocode(lat, lon);
        return ResponseEntity.ok(result);
    }

}

