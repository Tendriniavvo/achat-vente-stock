package com.example.backend_spring.services;

import com.example.backend_spring.models.LigneReception;
import com.example.backend_spring.repositories.LigneReceptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LigneReceptionService {
    private final LigneReceptionRepository ligneReceptionRepository;

    public List<LigneReception> getAllLignes() {
        return ligneReceptionRepository.findAll();
    }

    public Optional<LigneReception> getLigneById(int id) {
        return ligneReceptionRepository.findById(id);
    }

    @Transactional
    public LigneReception saveLigne(LigneReception ligne) {
        return ligneReceptionRepository.save(ligne);
    }

    @Transactional
    public void deleteLigne(int id) {
        ligneReceptionRepository.deleteById(id);
    }
}
