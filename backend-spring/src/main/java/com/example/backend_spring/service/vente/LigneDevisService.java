package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.LigneDevisRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneDevisService {

    private final LigneDevisRepository ligneDevisRepository;

    public LigneDevisService(LigneDevisRepository ligneDevisRepository) {
        this.ligneDevisRepository = ligneDevisRepository;
    }
}
