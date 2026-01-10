package com.example.backend_spring.service.partenaire;

import com.example.backend_spring.repository.partenaire.FournisseurRepository;
import org.springframework.stereotype.Service;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurService(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }
}
