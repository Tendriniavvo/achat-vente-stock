package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.LigneLivraisonRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneLivraisonService {

    private final LigneLivraisonRepository ligneLivraisonRepository;

    public LigneLivraisonService(LigneLivraisonRepository ligneLivraisonRepository) {
        this.ligneLivraisonRepository = ligneLivraisonRepository;
    }
}
