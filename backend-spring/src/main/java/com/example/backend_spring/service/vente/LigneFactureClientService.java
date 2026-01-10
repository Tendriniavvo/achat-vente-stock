package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.LigneFactureClientRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneFactureClientService {

    private final LigneFactureClientRepository ligneFactureClientRepository;

    public LigneFactureClientService(LigneFactureClientRepository ligneFactureClientRepository) {
        this.ligneFactureClientRepository = ligneFactureClientRepository;
    }
}
