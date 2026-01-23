package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneLivraison;
import com.example.backend_spring.repositories.LigneLivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneLivraisonService {
    private final LigneLivraisonRepository ligneLivraisonRepository;

    public List<LigneLivraison> getAllLignes() {
        return ligneLivraisonRepository.findAll();
    }

    public Optional<LigneLivraison> getLigneById(int id) {
        return ligneLivraisonRepository.findById(id);
    }

    @Transactional
    public LigneLivraison saveLigne(LigneLivraison ligne) {
        return ligneLivraisonRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneLivraisonRepository.deleteById(id);
    }
}
