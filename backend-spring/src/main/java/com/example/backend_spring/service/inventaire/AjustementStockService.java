package com.example.backend_spring.service.inventaire;

import com.example.backend_spring.repository.inventaire.AjustementStockRepository;
import org.springframework.stereotype.Service;

@Service
public class AjustementStockService {

    private final AjustementStockRepository ajustementStockRepository;

    public AjustementStockService(AjustementStockRepository ajustementStockRepository) {
        this.ajustementStockRepository = ajustementStockRepository;
    }
}
