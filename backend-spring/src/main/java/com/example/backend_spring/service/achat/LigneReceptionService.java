package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.LigneReceptionRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneReceptionService {

    private final LigneReceptionRepository ligneReceptionRepository;

    public LigneReceptionService(LigneReceptionRepository ligneReceptionRepository) {
        this.ligneReceptionRepository = ligneReceptionRepository;
    }
}
