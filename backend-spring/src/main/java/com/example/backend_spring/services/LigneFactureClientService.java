package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneFactureClient;
import com.example.backend_spring.repositories.LigneFactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneFactureClientService {
    private final LigneFactureClientRepository ligneFactureClientRepository;

    public List<LigneFactureClient> getAllLignes() {
        return ligneFactureClientRepository.findAll();
    }

    public Optional<LigneFactureClient> getLigneById(int id) {
        return ligneFactureClientRepository.findById(id);
    }

    @Transactional
    public LigneFactureClient saveLigne(LigneFactureClient ligne) {
        return ligneFactureClientRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneFactureClientRepository.deleteById(id);
    }
}
