package com.example.backend_spring.services;

import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.repositories.EmplacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmplacementService {
    private final EmplacementRepository emplacementRepository;

    public List<Emplacement> getAllEmplacements() {
        return emplacementRepository.findAll();
    }

    public Optional<Emplacement> getEmplacementById(int id) {
        return emplacementRepository.findById(id);
    }

    @Transactional
    public Emplacement saveEmplacement(Emplacement emplacement) {
        return emplacementRepository.save(emplacement);
    }

    @Transactional
    public void deleteEmplacement(int id) {
        emplacementRepository.deleteById(id);
    }
}
