package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.Repositories.ReclamationRepository;
import com.vimal.code.ToDo.Repositories.ServiceUrbainRepository;
import com.vimal.code.ToDo.dto.req.RequestReclamationDto;
import com.vimal.code.ToDo.models.Reclamation;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
public class ReclamationService {
    private final ReclamationRepository reclamationRepository;
    @Autowired
    private ReclamationMapper mapper;
    @Autowired
    private ServiceUrbainRepository serviceUrbainRepository;
    @Autowired
    private FileStorageService fileStorageService;


    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }

    public Reclamation getReclamationById(Long id) {
        return reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée !"));
    }

    public Reclamation createReclamation(RequestReclamationDto dto) throws IOException {
        Reclamation reclamation=mapper.toEntity(dto);
        return reclamationRepository.save(reclamation);
    }

    public Reclamation updateReclamation(Long id, RequestReclamationDto dto, MultipartFile imageFile) {
        return reclamationRepository.findById(id).map(rec -> {
            rec.setDescription(dto.getDescription());

            // 💾 Handle new image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String savedPath = fileStorageService.saveImage(imageFile); // Save image to disk
                    rec.setImage(savedPath); // Update image path in DB
                } catch (IOException e) {
                    throw new RuntimeException("❌ Failed to save new image: " + e.getMessage());
                }
            }

            // ✅ Update service type
            ServiceUrbain service = serviceUrbainRepository.findByType(dto.getServiceType());
            if (service == null) {
                throw new EntityNotFoundException("ServiceUrbain not found for type: " + dto.getServiceType());
            }
            rec.setServiceResponsable(service);
            rec.setDateCreation(new Date());

            return reclamationRepository.save(rec);
        }).orElseThrow(() -> new RuntimeException("Réclamation non trouvée !"));
    }


    public void deleteReclamation(Long id) {
        reclamationRepository.deleteById(id);
    }
    public List<Reclamation> getReclamationsByCitoyenId(Long citoyenId) {
        return reclamationRepository.findByCitoyenId(citoyenId);
    }

    public List<Reclamation> getReclamationsByService(ServiceType type) {
        ServiceUrbain service = serviceUrbainRepository.findByType(type);
        return reclamationRepository.findByServiceResponsable(service);
    }

    public Reclamation setReclamationAsResolved(Long id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée"));

        reclamation.setResolu(true);
        return reclamationRepository.save(reclamation);
    }

}
