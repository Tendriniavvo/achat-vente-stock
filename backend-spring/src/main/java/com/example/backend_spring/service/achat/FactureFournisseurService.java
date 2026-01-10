package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.FactureFournisseurRepository;
import org.springframework.stereotype.Service;

@Service
public class FactureFournisseurService {

    private final FactureFournisseurRepository factureFournisseurRepository;

    public FactureFournisseurService(FactureFournisseurRepository factureFournisseurRepository) {
        this.factureFournisseurRepository = factureFournisseurRepository;
    }
}
