package com.example.backend_spring.services;

import com.example.backend_spring.models.AjustementStock;
import com.example.backend_spring.repositories.AjustementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AjustementStockService {
    private final AjustementStockRepository ajustementStockRepository;

    public List<AjustementStock> getAllAjustements() {
        return ajustementStockRepository.findAll();
    }

    public Optional<AjustementStock> getAjustementById(int id) {
        return ajustementStockRepository.findById(id);
    }

    public Optional<AjustementStock> getAjustementByReference(String reference) {
        return ajustementStockRepository.findByReference(reference);
    }

    @Transactional
    public AjustementStock saveAjustement(AjustementStock ajustement) {
        return ajustementStockRepository.save(ajustement);
    }

    @Transactional
    public void deleteAjustement(int id) {
        ajustementStockRepository.deleteById(id);
    }
}
