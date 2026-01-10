package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.BonCommandeFournisseurRepository;
import org.springframework.stereotype.Service;

@Service
public class BonCommandeFournisseurService {

    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;

    public BonCommandeFournisseurService(BonCommandeFournisseurRepository bonCommandeFournisseurRepository) {
        this.bonCommandeFournisseurRepository = bonCommandeFournisseurRepository;
    }
}
