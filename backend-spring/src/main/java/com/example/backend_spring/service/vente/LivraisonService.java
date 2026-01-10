package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.LivraisonRepository;
import org.springframework.stereotype.Service;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;

    public LivraisonService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }
}
