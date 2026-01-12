package com.example.backend_spring.service.stock;

import com.example.backend_spring.dto.stock.EmplacementRequest;
import com.example.backend_spring.dto.stock.EmplacementResponse;
import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.Emplacement;
import com.example.backend_spring.repository.stock.DepotRepository;
import com.example.backend_spring.repository.stock.EmplacementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmplacementService {

    @Autowired
    private EmplacementRepository emplacementRepository;

    @Autowired
    private DepotRepository depotRepository;

    // Créer un nouvel emplacement
    @Transactional
    public EmplacementResponse createEmplacement(EmplacementRequest request) {
        Depot depot = depotRepository.findById(request.getDepotId())
                .orElseThrow(() -> new RuntimeException("Dépôt introuvable avec l'ID: " + request.getDepotId()));

        Emplacement emplacement = new Emplacement();
        emplacement.setDepot(depot);
        emplacement.setCode(request.getCode());
        emplacement.setDescription(request.getDescription());
        emplacement.setCapacite(request.getCapacite());

        Emplacement savedEmplacement = emplacementRepository.save(emplacement);
        return new EmplacementResponse(savedEmplacement);
    }

    // Récupérer tous les emplacements
    public List<EmplacementResponse> getAllEmplacements() {
        return emplacementRepository.findAll().stream()
                .map(EmplacementResponse::new)
                .collect(Collectors.toList());
    }

    // Récupérer les emplacements d'un dépôt
    public List<EmplacementResponse> getEmplacementsByDepot(Integer depotId) {
        return emplacementRepository.findByDepotId(depotId).stream()
                .map(EmplacementResponse::new)
                .collect(Collectors.toList());
    }

    // Récupérer un emplacement par ID
    public EmplacementResponse getEmplacementById(Integer id) {
        Emplacement emplacement = emplacementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement introuvable avec l'ID: " + id));
        return new EmplacementResponse(emplacement);
    }

    // Mettre à jour un emplacement
    @Transactional
    public EmplacementResponse updateEmplacement(Integer id, EmplacementRequest request) {
        Emplacement emplacement = emplacementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement introuvable avec l'ID: " + id));

        if (request.getDepotId() != null && !request.getDepotId().equals(emplacement.getDepot().getId())) {
            Depot depot = depotRepository.findById(request.getDepotId())
                    .orElseThrow(() -> new RuntimeException("Dépôt introuvable avec l'ID: " + request.getDepotId()));
            emplacement.setDepot(depot);
        }

        emplacement.setCode(request.getCode());
        emplacement.setDescription(request.getDescription());
        emplacement.setCapacite(request.getCapacite());

        Emplacement updatedEmplacement = emplacementRepository.save(emplacement);
        return new EmplacementResponse(updatedEmplacement);
    }

    // Supprimer un emplacement
    @Transactional
    public void deleteEmplacement(Integer id) {
        if (!emplacementRepository.existsById(id)) {
            throw new RuntimeException("Emplacement introuvable avec l'ID: " + id);
        }
        emplacementRepository.deleteById(id);
    }

    // Rechercher des emplacements par code
    public List<EmplacementResponse> searchEmplacementsByCode(String code) {
        return emplacementRepository.findByCodeContainingIgnoreCase(code).stream()
                .map(EmplacementResponse::new)
                .collect(Collectors.toList());
    }

    // Rechercher des emplacements par code et dépôt
    public List<EmplacementResponse> searchEmplacementsByCodeAndDepot(Integer depotId, String code) {
        return emplacementRepository.findByDepotIdAndCodeContainingIgnoreCase(depotId, code).stream()
                .map(EmplacementResponse::new)
                .collect(Collectors.toList());
    }
}
