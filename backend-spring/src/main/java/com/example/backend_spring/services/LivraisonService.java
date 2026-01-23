package com.example.backend_spring.services;

import com.example.backend_spring.models.Livraison;
import com.example.backend_spring.repositories.LivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivraisonService {
    private final LivraisonRepository livraisonRepository;

    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }

    public Optional<Livraison> getLivraisonById(int id) {
        return livraisonRepository.findById(id);
    }

    public Optional<Livraison> getLivraisonByReference(String reference) {
        return livraisonRepository.findByReference(reference);
    }

    @Transactional
    public Livraison saveLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Transactional
    public void deleteLivraison(int id) {
        livraisonRepository.deleteById(id);
    }
}
