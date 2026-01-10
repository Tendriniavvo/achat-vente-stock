package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.LigneCommandeClientRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneCommandeClientService {

    private final LigneCommandeClientRepository ligneCommandeClientRepository;

    public LigneCommandeClientService(LigneCommandeClientRepository ligneCommandeClientRepository) {
        this.ligneCommandeClientRepository = ligneCommandeClientRepository;
    }
}
