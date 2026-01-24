package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneBonCommande;
import com.example.backend_spring.repositories.LigneBonCommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneBonCommandeService {
    private final LigneBonCommandeRepository ligneBonCommandeRepository;

    public List<LigneBonCommande> getAllLignes() {
        return ligneBonCommandeRepository.findAll();
    }

    public Optional<LigneBonCommande> getLigneById(int id) {
        return ligneBonCommandeRepository.findById(id);
    }

    @Transactional
    public LigneBonCommande saveLigne(LigneBonCommande ligne) {
        return ligneBonCommandeRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneBonCommandeRepository.deleteById(id);
    }
}
