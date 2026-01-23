package com.example.backend_spring.services;

import com.example.backend_spring.models.FactureClient;
import com.example.backend_spring.repositories.FactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureClientService {

    private final FactureClientRepository factureClientRepository;

    public List<FactureClient> getAllFactures() {
        return factureClientRepository.findAll();
    }

    public Optional<FactureClient> getFactureById(int id) {
        return factureClientRepository.findById(id);
    }

    public Optional<FactureClient> getFactureByReference(String reference) {
        return factureClientRepository.findByReference(reference);
    }

    @Transactional
    public FactureClient saveFacture(FactureClient facture) {
        return factureClientRepository.save(facture);
    }

    @Transactional
    public void deleteFacture(int id) {
        factureClientRepository.deleteById(id);
    }
}
