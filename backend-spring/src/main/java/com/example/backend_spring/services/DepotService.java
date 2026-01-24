package com.example.backend_spring.services;

import com.example.backend_spring.models.Depot;
import com.example.backend_spring.repositories.DepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepotService {
    private final DepotRepository depotRepository;

    public List<Depot> getAllDepots() {
        return depotRepository.findAll();
    }

    public Optional<Depot> getDepotById(int id) {
        return depotRepository.findById(id);
    }

    @Transactional
    public Depot saveDepot(Depot depot) {
        return depotRepository.save(depot);
    }

    @Transactional
    public Depot updateDepot(int id, Depot depotDetails) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépôt non trouvé avec l'id : " + id));
        
        depot.setNom(depotDetails.getNom());
        depot.setCode(depotDetails.getCode());
        depot.setAdresse(depotDetails.getAdresse());
        depot.setResponsable(depotDetails.getResponsable());
        depot.setCapacite(depotDetails.getCapacite());
        depot.setTypeEntreposage(depotDetails.getTypeEntreposage());
        depot.setHorairesOuverture(depotDetails.getHorairesOuverture());
        depot.setActif(depotDetails.isActif());
        
        return depotRepository.save(depot);
    }

    @Transactional
    public void toggleStatus(int id) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépôt non trouvé avec l'id : " + id));
        depot.setActif(!depot.isActif());
        depotRepository.save(depot);
    }

    @Transactional
    public void deleteDepot(int id) {
        depotRepository.deleteById(id);
    }
}
