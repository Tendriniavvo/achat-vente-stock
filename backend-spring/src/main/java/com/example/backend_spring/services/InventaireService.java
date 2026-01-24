package com.example.backend_spring.services;

import com.example.backend_spring.models.Inventaire;
import com.example.backend_spring.repositories.InventaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventaireService {
    private final InventaireRepository inventaireRepository;

    public List<Inventaire> getAllInventaires() {
        return inventaireRepository.findAll();
    }

    public Optional<Inventaire> getInventaireById(int id) {
        return inventaireRepository.findById(id);
    }

    public Optional<Inventaire> getInventaireByReference(String reference) {
        return inventaireRepository.findByReference(reference);
    }

    @Transactional
    public Inventaire saveInventaire(Inventaire inventaire) {
        return inventaireRepository.save(inventaire);
    }

    @Transactional
    public void deleteInventaire(int id) {
        inventaireRepository.deleteById(id);
    }
}
