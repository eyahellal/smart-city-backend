package com.vimal.code.ToDo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/citoyen")
@Slf4j
public class ChatbotController {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${google.ai.api.key}")
    private String apiKey;

    public ChatbotController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chatApi(@RequestBody ChatRequest chatRequest) {
        String userMessage = chatRequest.getMessage();
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Message cannot be empty");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Add system prompt for Harmonia smart city app
        String systemPrompt = "ous etes votre assistant virtuel pour l'application Smart City tunisienne .\n" +
                "Dans cette application, les citoyens peuvent :\n" +
                "\n" +
                "Signaler des problèmes urbains en créant des réclamations géolocalisées sur la carte de la ville ;\n" +
                "\n" +
                "Consulter, organiser ou participer à des événements communautaires ;\n" +
                "\n" +
                "Soumettre des problèmes techniques liés à l’application directement à l’administrateur ;\n" +
                "\n" +
                "Explorer la ville à l’aide d’une carte interactive dynamique. Si la  requete en dehord de ce sujet,poliment clarifiez votre role en tant que assistant dans smart city et excusez vous de ne  pas pouvoir repondre ";


        // Complete message with system prompt
        String fullMessage = systemPrompt + " User: " + userMessage;

        try {
            // Create request body structure
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            content.put("role", "user");
            
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", fullMessage);
            parts.add(part);
            
            content.set("parts", parts);
            contents.add(content);
            requestBody.set("contents", contents);
            
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.set("generationConfig", generationConfig);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // Make the API request
            ResponseEntity<String> response = restTemplate.exchange(
                API_URL + "?key=" + apiKey,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                
                if (responseBody.has("candidates") && 
                    responseBody.get("candidates").isArray() && 
                    responseBody.get("candidates").size() > 0) {
                    
                    JsonNode candidate = responseBody.get("candidates").get(0);
                    if (candidate.has("content") && 
                        candidate.get("content").has("parts") && 
                        candidate.get("content").get("parts").isArray() &&
                        candidate.get("content").get("parts").size() > 0) {
                        
                        String responseText = candidate.get("content").get("parts").get(0).get("text").asText();
                        Map<String, String> chatResponse = new HashMap<>();
                        chatResponse.put("response", responseText);
                        return ResponseEntity.ok(chatResponse);
                    }
                }
                
                // Log the full response structure to help debug
                log.error("Unexpected API response structure: {}", responseBody);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Could not extract response from API.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "HTTP error: " + response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Error in chatbot API call", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Data
    public static class ChatRequest {
        private String message;
    }
}
