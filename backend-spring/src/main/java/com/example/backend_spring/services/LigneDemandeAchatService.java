package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneDemandeAchat;
import com.example.backend_spring.repositories.LigneDemandeAchatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneDemandeAchatService {
    private final LigneDemandeAchatRepository ligneDemandeAchatRepository;

    public List<LigneDemandeAchat> getAllLignes() {
        return ligneDemandeAchatRepository.findAll();
    }

    public Optional<LigneDemandeAchat> getLigneById(int id) {
        return ligneDemandeAchatRepository.findById(id);
    }

    @Transactional
    public LigneDemandeAchat saveLigne(LigneDemandeAchat ligne) {
        return ligneDemandeAchatRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneDemandeAchatRepository.deleteById(id);
    }
}
