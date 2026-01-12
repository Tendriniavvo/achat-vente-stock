package com.example.backend_spring.service.stock;

import com.example.backend_spring.dto.stock.DepotRequest;
import com.example.backend_spring.dto.stock.DepotResponse;
import com.example.backend_spring.model.Depot;
import com.example.backend_spring.repository.stock.DepotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepotService {

    @Autowired
    private DepotRepository depotRepository;

    // Créer un nouveau dépôt
    @Transactional
    public DepotResponse createDepot(DepotRequest request) {
        Depot depot = new Depot();
        depot.setNom(request.getNom());
        depot.setAdresse(request.getAdresse());
        depot.setCapacite(request.getCapacite());
        depot.setActif(request.getActif() != null ? request.getActif() : true);

        Depot savedDepot = depotRepository.save(depot);
        return new DepotResponse(savedDepot);
    }

    // Récupérer tous les dépôts
    public List<DepotResponse> getAllDepots() {
        return depotRepository.findAll().stream()
                .map(DepotResponse::new)
                .collect(Collectors.toList());
    }

    // Récupérer les dépôts actifs uniquement
    public List<DepotResponse> getDepotsActifs() {
        return depotRepository.findByActif(true).stream()
                .map(DepotResponse::new)
                .collect(Collectors.toList());
    }

    // Récupérer un dépôt par ID
    public DepotResponse getDepotById(Integer id) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépôt introuvable avec l'ID: " + id));
        return new DepotResponse(depot);
    }

    // Mettre à jour un dépôt
    @Transactional
    public DepotResponse updateDepot(Integer id, DepotRequest request) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépôt introuvable avec l'ID: " + id));

        depot.setNom(request.getNom());
        depot.setAdresse(request.getAdresse());
        depot.setCapacite(request.getCapacite());
        if (request.getActif() != null) {
            depot.setActif(request.getActif());
        }

        Depot updatedDepot = depotRepository.save(depot);
        return new DepotResponse(updatedDepot);
    }

    // Supprimer un dépôt
    @Transactional
    public void deleteDepot(Integer id) {
        if (!depotRepository.existsById(id)) {
            throw new RuntimeException("Dépôt introuvable avec l'ID: " + id);
        }
        depotRepository.deleteById(id);
    }

    // Activer/Désactiver un dépôt
    @Transactional
    public DepotResponse toggleActif(Integer id) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépôt introuvable avec l'ID: " + id));

        depot.setActif(!depot.getActif());
        Depot updatedDepot = depotRepository.save(depot);
        return new DepotResponse(updatedDepot);
    }

    // Rechercher des dépôts par nom
    public List<DepotResponse> searchDepotsByNom(String nom) {
        return depotRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(DepotResponse::new)
                .collect(Collectors.toList());
    }
}
