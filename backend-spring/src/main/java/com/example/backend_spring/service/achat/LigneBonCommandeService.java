package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.LigneBonCommandeRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneBonCommandeService {

    private final LigneBonCommandeRepository ligneBonCommandeRepository;

    public LigneBonCommandeService(LigneBonCommandeRepository ligneBonCommandeRepository) {
        this.ligneBonCommandeRepository = ligneBonCommandeRepository;
    }
}
