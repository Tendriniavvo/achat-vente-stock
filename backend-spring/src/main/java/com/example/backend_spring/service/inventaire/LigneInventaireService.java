package com.example.backend_spring.service.inventaire;

import com.example.backend_spring.repository.inventaire.LigneInventaireRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneInventaireService {

    private final LigneInventaireRepository ligneInventaireRepository;

    public LigneInventaireService(LigneInventaireRepository ligneInventaireRepository) {
        this.ligneInventaireRepository = ligneInventaireRepository;
    }
}
