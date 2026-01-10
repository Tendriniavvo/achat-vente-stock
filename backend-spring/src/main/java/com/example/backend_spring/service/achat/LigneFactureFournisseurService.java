package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.LigneFactureFournisseurRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneFactureFournisseurService {

    private final LigneFactureFournisseurRepository ligneFactureFournisseurRepository;

    public LigneFactureFournisseurService(LigneFactureFournisseurRepository ligneFactureFournisseurRepository) {
        this.ligneFactureFournisseurRepository = ligneFactureFournisseurRepository;
    }
}
