package com.example.backend_spring.services;

import com.example.backend_spring.models.ValorisationStock;
import com.example.backend_spring.repositories.ValorisationStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValorisationStockService {
    private final ValorisationStockRepository valorisationStockRepository;

    public List<ValorisationStock> getAllValorisations() {
        return valorisationStockRepository.findAll();
    }

    public Optional<ValorisationStock> getValorisationById(int id) {
        return valorisationStockRepository.findById(id);
    }

    @Transactional
    public ValorisationStock saveValorisation(ValorisationStock valorisation) {
        return valorisationStockRepository.save(valorisation);
    }

    @Transactional
    public void deleteValorisation(int id) {
        valorisationStockRepository.deleteById(id);
    }
}
