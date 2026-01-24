package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneInventaire;
import com.example.backend_spring.repositories.LigneInventaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneInventaireService {
    private final LigneInventaireRepository ligneInventaireRepository;

    public List<LigneInventaire> getAllLignes() {
        return ligneInventaireRepository.findAll();
    }

    public Optional<LigneInventaire> getLigneById(int id) {
        return ligneInventaireRepository.findById(id);
    }

    @Transactional
    public LigneInventaire saveLigne(LigneInventaire ligne) {
        return ligneInventaireRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneInventaireRepository.deleteById(id);
    }
}
