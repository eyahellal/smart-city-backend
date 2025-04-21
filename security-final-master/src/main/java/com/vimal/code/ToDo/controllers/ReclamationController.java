package com.vimal.code.ToDo.controllers;

import com.vimal.code.ToDo.dto.req.RequestReclamationDto;
import com.vimal.code.ToDo.dto.resp.ResponseReclamationDto;
import com.vimal.code.ToDo.models.Agent;
import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import com.vimal.code.ToDo.service.AgentService;
import com.vimal.code.ToDo.service.ReclamationService;
import com.vimal.code.ToDo.service.ReclamationMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reclamations")
//@PreAuthorize("hasRole('ROLE_CITOYEN')")


@RequiredArgsConstructor
public class ReclamationController {
    private final ReclamationService reclamationService;
    private final AgentService agentService;
    private final ReclamationMapper reclamationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ReclamationController.class);


    @PutMapping("/agent/setResolu/{id}")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<?> setReclamationResolu(@PathVariable Long id) {
        try {
            Reclamation updated = reclamationService.setReclamationAsResolved(id);
            return ResponseEntity.ok("✅ Réclamation marquée comme résolue avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Réclamation non trouvée.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Erreur lors de la mise à jour de la réclamation.");
        }
    }




    @GetMapping("/agent/getAll")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<?> getReclamationsByAgentService(Authentication authentication) {
        try {
            String agentEmail = authentication.getName();
            System.out.println("✅ Authenticated agent email: " + agentEmail);

            Optional<Agent> agentOpt = agentService.findByEmail(agentEmail);

            if (agentOpt.isEmpty()) {
                System.out.println("❌ Agent not found for email: " + agentEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Agent non trouvé pour l'email : " + agentEmail);
            }

            Agent agent = agentOpt.get();
            ServiceUrbain service = agent.getServiceUrbain();

            if (service == null) {
                System.out.println("⚠️ Agent found but has no associated service.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("L'agent n'est pas associé à un service urbain.");
            }

            System.out.println("📌 Agent is linked to service: " + service.getType());

            List<Reclamation> reclamations = reclamationService.getReclamationsByService(service.getType());

            System.out.println("📥 Nombre de réclamations récupérées: " + reclamations.size());

            List<ResponseReclamationDto> response = reclamations.stream()
                    .map(reclamationMapper::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // Optional: log for debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur lors de la récupération des réclamations : " + e.getMessage());
        }
    }




    @GetMapping("/getAll/{id}")
    public ResponseEntity<List<ResponseReclamationDto>> getAllReclamationsByCitoyen(@PathVariable Long id) {
        List<ResponseReclamationDto> reclamations = reclamationService.getReclamationsByCitoyenId(id)
                .stream()
                .map(reclamationMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reclamations);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseReclamationDto> getReclamationById(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationMapper.toDto(reclamationService.getReclamationById(id)));
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createReclamation(
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam("description") String description,
            @RequestParam("serviceType") String serviceType,
            Authentication authentication) {

        try {
            // Check if serviceType is provided
            if (serviceType == null || serviceType.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Service type is required.");
            }

            // Prepare DTO
            RequestReclamationDto requestDto = new RequestReclamationDto();
            requestDto.setImage(imageFile);
            requestDto.setDescription(description);
            requestDto.setServiceType(ServiceType.valueOf(serviceType));

            // Create Reclamation
            Reclamation createdReclamation = reclamationService.createReclamation(requestDto);
            ResponseReclamationDto responseDto = reclamationMapper.toDto(createdReclamation);

            logger.info("✅ Reclamation created successfully for user: {}", authentication.getName());
            System.out.println("📸 Received image: " + imageFile.getOriginalFilename());
            System.out.println("📏 Image size: " + imageFile.getSize());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (EntityNotFoundException e) {
            logger.error("❌ Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IOException e) {
            e.printStackTrace(); // Show full cause in console
            return ResponseEntity.badRequest().body("Error processing image file: " + e.getMessage());

        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating reclamation: " + e.getMessage());
        }
    }

    /**
     * Retrieve an image by reclamation ID
     */
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        Reclamation reclamation = reclamationService.getReclamationById(id);
        if (reclamation.getImage() == null) {
            logger.warn("Image not found for reclamation ID: {}", id);
            throw new EntityNotFoundException("No image found for reclamation ID: " + id);
        }

        Path filePath = Paths.get("uploads", reclamation.getImage());
        if (!Files.exists(filePath)) {
            logger.warn("Image file not found at path: {}", filePath);
            throw new EntityNotFoundException("Image file not found for reclamation ID: " + id);
        }

        byte[] imageData = Files.readAllBytes(filePath);
        logger.debug("Successfully retrieved image for reclamation ID: {}", id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Adjust based on image type
                .body(imageData);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReclamation(
            @PathVariable Long id,
            @RequestParam("description") String description,
            @RequestParam("serviceType") ServiceType serviceType,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            RequestReclamationDto dto = new RequestReclamationDto();
            dto.setDescription(description);
            dto.setServiceType(serviceType);
            Reclamation updated = reclamationService.updateReclamation(id, dto, image);
            return ResponseEntity.ok(reclamationMapper.toDto(updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating reclamation: " + e.getMessage());
        }
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteReclamation(
            @PathVariable Long id)
     {
        try {
            reclamationService.deleteReclamation(id);
            logger.info("Successfully deleted reclamation ID: {}", id);
            return ResponseEntity.ok("Reclamation deleted successfully");
        } catch (EntityNotFoundException e) {
            logger.warn("Reclamation not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }}}


