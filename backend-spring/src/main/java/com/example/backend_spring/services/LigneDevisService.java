package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneDevis;
import com.example.backend_spring.repositories.LigneDevisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneDevisService {
    private final LigneDevisRepository ligneDevisRepository;

    public List<LigneDevis> getAllLignes() {
        return ligneDevisRepository.findAll();
    }

    public Optional<LigneDevis> getLigneById(int id) {
        return ligneDevisRepository.findById(id);
    }

    @Transactional
    public LigneDevis saveLigne(LigneDevis ligne) {
        return ligneDevisRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneDevisRepository.deleteById(id);
    }
}
