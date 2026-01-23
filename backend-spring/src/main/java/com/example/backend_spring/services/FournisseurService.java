package com.example.backend_spring.services;

import com.example.backend_spring.models.Fournisseur;
import com.example.backend_spring.repositories.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurRepository.findAll();
    }

    public Optional<Fournisseur> getFournisseurById(int id) {
        return fournisseurRepository.findById(id);
    }

    @Transactional
    public Fournisseur saveFournisseur(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    @Transactional
    public void deleteFournisseur(int id) {
        fournisseurRepository.deleteById(id);
    }
}
