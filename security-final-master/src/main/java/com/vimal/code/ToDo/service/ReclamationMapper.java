package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.Repositories.ServiceUrbainRepository;
import com.vimal.code.ToDo.dto.req.RequestReclamationDto;
import com.vimal.code.ToDo.dto.resp.ResponseReclamationDto;
import com.vimal.code.ToDo.models.UserEnitiy;
import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceUrbain;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@AllArgsConstructor
@Service
public class ReclamationMapper {

    private final ServiceUrbainRepository serviceUrbainRepository; // Injected properly
    @Autowired
    private CitoyenService citoyenService;
    @Autowired
    private UserService userService;
    @Autowired
    FileStorageService fileStorageService;

    /**
     * Convert Request DTO to Reclamation Entity
     */
    public Reclamation toEntity(RequestReclamationDto dto/* ,Citoyen citoyen*/) throws IOException {
        Reclamation reclamation = new Reclamation();
        reclamation.setResolu(false);
        reclamation.setDescription(dto.getDescription());
        reclamation.setDateCreation(new Date());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String email = authentication.getName();
            System.out.println("🔍 Authenticated user email: " + email);

            UserEnitiy citoyen = userService.findByEmail(email);
            if (citoyen != null) {
                System.out.println("✅ Found citoyen: " + citoyen.getName() + " with ID: " + citoyen.getId());
                reclamation.setCitoyen(citoyen);
                // Verify it was set
                System.out.println("✅ Set citoyen on reclamation: " + reclamation.getCitoyen().getName());
            } else {
                System.out.println("❌ No citoyen found for email: " + email);
            }
        } else {
            System.out.println("❌ No authentication or username available");
        }
        assert authentication != null;
        UserEnitiy citoyen =  userService.findByEmail(authentication.getName());
            reclamation.setCitoyen(citoyen);


        // Save image file and set filename
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String filename = fileStorageService.saveImage(dto.getImage());
            reclamation.setImage(filename);
        }


        // Find the responsible ServiceUrbain by type

        ServiceUrbain service = serviceUrbainRepository.findByType(dto.getServiceType());
        if (service == null) {
            throw new EntityNotFoundException("ServiceUrbain not found for type: " + dto.getServiceType());
        }

        reclamation.setServiceResponsable(service);

        return reclamation;
    }

    /**
     * Convert Reclamation Entity to Response DTO
     */
    public ResponseReclamationDto toDto(Reclamation reclamation) {
        ResponseReclamationDto dto = new ResponseReclamationDto();
        dto.setId(reclamation.getId());
        dto.setResolu(reclamation.isResolu());
        dto.setImage(reclamation.getImage());
        dto.setDescription(reclamation.getDescription());
        dto.setDateCreation(reclamation.getDateCreation());


        // Extract details from entity
     // dto.setCitoyenName(reclamation.getCitoyen().getName());
        dto.setServiceUrbainType(reclamation.getServiceResponsable().getType().toString());

        return dto;
    }
}
