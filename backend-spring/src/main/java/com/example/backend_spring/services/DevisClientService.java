package com.example.backend_spring.services;

import com.example.backend_spring.models.DevisClient;
import com.example.backend_spring.repositories.DevisClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevisClientService {
    private final DevisClientRepository devisClientRepository;

    public List<DevisClient> getAllDevis() {
        return devisClientRepository.findAll();
    }

    public Optional<DevisClient> getDevisById(int id) {
        return devisClientRepository.findById(id);
    }

    public Optional<DevisClient> getDevisByReference(String reference) {
        return devisClientRepository.findByReference(reference);
    }

    @Transactional
    public DevisClient saveDevis(DevisClient devis) {
        return devisClientRepository.save(devis);
    }

    @Transactional
    public void deleteDevis(int id) {
        devisClientRepository.deleteById(id);
    }
}
