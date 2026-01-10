package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.FactureClientRepository;
import org.springframework.stereotype.Service;

@Service
public class FactureClientService {

    private final FactureClientRepository factureClientRepository;

    public FactureClientService(FactureClientRepository factureClientRepository) {
        this.factureClientRepository = factureClientRepository;
    }
}
