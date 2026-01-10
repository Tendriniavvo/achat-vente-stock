package com.example.backend_spring.service.stock;

import com.example.backend_spring.repository.stock.EmplacementRepository;
import org.springframework.stereotype.Service;

@Service
public class EmplacementService {

    private final EmplacementRepository emplacementRepository;

    public EmplacementService(EmplacementRepository emplacementRepository) {
        this.emplacementRepository = emplacementRepository;
    }
}
