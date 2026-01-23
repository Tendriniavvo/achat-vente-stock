package com.example.backend_spring.services;

import com.example.backend_spring.models.Encaissement;
import com.example.backend_spring.repositories.EncaissementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EncaissementService {
    private final EncaissementRepository encaissementRepository;

    public List<Encaissement> getAllEncaissements() {
        return encaissementRepository.findAll();
    }

    public Optional<Encaissement> getEncaissementById(int id) {
        return encaissementRepository.findById(id);
    }

    @Transactional
    public Encaissement saveEncaissement(Encaissement encaissement) {
        return encaissementRepository.save(encaissement);
    }

    @Transactional
    public void deleteEncaissement(int id) {
        encaissementRepository.deleteById(id);
    }
}
