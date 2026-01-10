package com.example.backend_spring.service.inventaire;

import com.example.backend_spring.repository.inventaire.InventaireRepository;
import org.springframework.stereotype.Service;

@Service
public class InventaireService {

    private final InventaireRepository inventaireRepository;

    public InventaireService(InventaireRepository inventaireRepository) {
        this.inventaireRepository = inventaireRepository;
    }
}
