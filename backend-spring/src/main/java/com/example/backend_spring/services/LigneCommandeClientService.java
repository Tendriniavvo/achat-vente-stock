package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneCommandeClient;
import com.example.backend_spring.repositories.LigneCommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneCommandeClientService {
    private final LigneCommandeClientRepository ligneCommandeClientRepository;

    public List<LigneCommandeClient> getAllLignes() {
        return ligneCommandeClientRepository.findAll();
    }

    public Optional<LigneCommandeClient> getLigneById(int id) {
        return ligneCommandeClientRepository.findById(id);
    }

    @Transactional
    public LigneCommandeClient saveLigne(LigneCommandeClient ligne) {
        return ligneCommandeClientRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneCommandeClientRepository.deleteById(id);
    }
}
