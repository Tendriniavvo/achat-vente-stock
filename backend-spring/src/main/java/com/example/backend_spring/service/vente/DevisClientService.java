package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.DevisClientRepository;
import org.springframework.stereotype.Service;

@Service
public class DevisClientService {

    private final DevisClientRepository devisClientRepository;

    public DevisClientService(DevisClientRepository devisClientRepository) {
        this.devisClientRepository = devisClientRepository;
    }
}
