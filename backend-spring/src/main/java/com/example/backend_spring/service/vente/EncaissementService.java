package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.EncaissementRepository;
import org.springframework.stereotype.Service;

@Service
public class EncaissementService {

    private final EncaissementRepository encaissementRepository;

    public EncaissementService(EncaissementRepository encaissementRepository) {
        this.encaissementRepository = encaissementRepository;
    }
}
