package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneFactureFournisseur;
import com.example.backend_spring.repositories.LigneFactureFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneFactureFournisseurService {
    private final LigneFactureFournisseurRepository ligneFactureFournisseurRepository;

    public List<LigneFactureFournisseur> getAllLignes() {
        return ligneFactureFournisseurRepository.findAll();
    }

    public Optional<LigneFactureFournisseur> getLigneById(int id) {
        return ligneFactureFournisseurRepository.findById(id);
    }

    @Transactional
    public LigneFactureFournisseur saveLigne(LigneFactureFournisseur ligne) {
        return ligneFactureFournisseurRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneFactureFournisseurRepository.deleteById(id);
    }
}
