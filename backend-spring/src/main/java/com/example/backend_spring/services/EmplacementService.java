package com.example.backend_spring.services;

import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.repositories.EmplacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmplacementService {
    private final EmplacementRepository emplacementRepository;

    public List<Emplacement> getAllEmplacements() {
        return emplacementRepository.findAll();
    }

    public Optional<Emplacement> getEmplacementById(int id) {
        return emplacementRepository.findById(id);
    }

    public List<Emplacement> getEmplacementsByDepot(int depotId) {
        return emplacementRepository.findByDepotId(depotId);
    }

    public List<Emplacement> getRootEmplacementsByDepot(int depotId) {
        return emplacementRepository.findByDepotIdAndParentIsNull(depotId);
    }

    public List<Emplacement> getChildren(int parentId) {
        return emplacementRepository.findByParentId(parentId);
    }

    @Transactional
    public Emplacement saveEmplacement(Emplacement emplacement) {
        // Logique de génération automatique de code si non fourni
        if (emplacement.getCode() == null || emplacement.getCode().isEmpty()) {
            emplacement.setCode(generateCode(emplacement));
        }
        return emplacementRepository.save(emplacement);
    }

    @Transactional
    public Emplacement updateEmplacement(int id, Emplacement details) {
        Emplacement emplacement = emplacementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emplacement non trouvé"));
        
        emplacement.setCode(details.getCode());
        emplacement.setTypeEmplacement(details.getTypeEmplacement());
        emplacement.setDescription(details.getDescription());
        emplacement.setCapacite(details.getCapacite());
        emplacement.setCaracteristiques(details.getCaracteristiques());
        emplacement.setConditionsStockage(details.getConditionsStockage());
        emplacement.setTypesProduitsAcceptes(details.getTypesProduitsAcceptes());
        emplacement.setParent(details.getParent());
        
        return emplacementRepository.save(emplacement);
    }

    private String generateCode(Emplacement e) {
        StringBuilder sb = new StringBuilder();
        if (e.getParent() != null) {
            // Recharger le parent pour avoir son code si nécessaire
            Emplacement parent = emplacementRepository.findById(e.getParent().getId()).orElse(null);
            if (parent != null) {
                sb.append(parent.getCode()).append("-");
            }
        }
        
        String libelle = e.getTypeEmplacement().getLibelle();
        String prefix = libelle.substring(0, 1).toUpperCase();
        // Compter combien de frères du même type existent sous ce parent
        long count;
        if (e.getParent() != null) {
            count = emplacementRepository.findByParentId(e.getParent().getId()).stream()
                    .filter(child -> child.getTypeEmplacement().getLibelle().equals(libelle))
                    .count();
        } else {
            count = emplacementRepository.findByDepotIdAndParentIsNull(e.getDepot().getId()).stream()
                    .filter(root -> root.getTypeEmplacement().getLibelle().equals(libelle))
                    .count();
        }
        
        sb.append(prefix).append(count + 1);
        return sb.toString();
    }

    @Transactional
    public void deleteEmplacement(int id) {
        emplacementRepository.deleteById(id);
    }
}
