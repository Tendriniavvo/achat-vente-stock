package com.example.backend_spring.services;

import com.example.backend_spring.models.FactureFournisseur;
import com.example.backend_spring.repositories.FactureFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureFournisseurService {

    private final FactureFournisseurRepository factureFournisseurRepository;

    public List<FactureFournisseur> getAllFactures() {
        return factureFournisseurRepository.findAll();
    }

    public Optional<FactureFournisseur> getFactureById(int id) {
        return factureFournisseurRepository.findById(id);
    }

    public Optional<FactureFournisseur> getFactureByReference(String reference) {
        return factureFournisseurRepository.findByReference(reference);
    }

    @Transactional
    public FactureFournisseur saveFacture(FactureFournisseur facture) {
        return factureFournisseurRepository.save(facture);
    }

    @Transactional
    public void deleteFacture(int id) {
        factureFournisseurRepository.deleteById(id);
    }
}
