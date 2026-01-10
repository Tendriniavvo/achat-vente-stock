package com.example.backend_spring.service.inventaire;

import com.example.backend_spring.repository.inventaire.ValorisationStockRepository;
import org.springframework.stereotype.Service;

@Service
public class ValorisationStockService {

    private final ValorisationStockRepository valorisationStockRepository;

    public ValorisationStockService(ValorisationStockRepository valorisationStockRepository) {
        this.valorisationStockRepository = valorisationStockRepository;
    }
}
