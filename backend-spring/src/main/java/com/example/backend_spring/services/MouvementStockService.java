package com.example.backend_spring.services;

import com.example.backend_spring.models.MouvementStock;
import com.example.backend_spring.repositories.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;

    public List<MouvementStock> getAllMouvements() {
        return mouvementStockRepository.findAll();
    }

    public Optional<MouvementStock> getMouvementById(int id) {
        return mouvementStockRepository.findById(id);
    }

    @Transactional
    public MouvementStock saveMouvement(MouvementStock mouvement) {
        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public void deleteMouvement(int id) {
        mouvementStockRepository.deleteById(id);
    }
}
