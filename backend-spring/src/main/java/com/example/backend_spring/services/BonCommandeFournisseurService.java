package com.example.backend_spring.services;

import com.example.backend_spring.models.BonCommandeFournisseur;
import com.example.backend_spring.repositories.BonCommandeFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BonCommandeFournisseurService {

    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;

    public List<BonCommandeFournisseur> getAllBonCommandes() {
        return bonCommandeFournisseurRepository.findAll();
    }

    public Optional<BonCommandeFournisseur> getBonCommandeById(int id) {
        return bonCommandeFournisseurRepository.findById(id);
    }

    public Optional<BonCommandeFournisseur> getBonCommandeByReference(String reference) {
        return bonCommandeFournisseurRepository.findByReference(reference);
    }

    @Transactional
    public BonCommandeFournisseur saveBonCommande(BonCommandeFournisseur bonCommande) {
        return bonCommandeFournisseurRepository.save(bonCommande);
    }

    @Transactional
    public void deleteBonCommande(int id) {
        bonCommandeFournisseurRepository.deleteById(id);
    }
}
