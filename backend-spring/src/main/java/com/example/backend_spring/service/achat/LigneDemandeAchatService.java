package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.LigneDemandeAchatRepository;
import org.springframework.stereotype.Service;

@Service
public class LigneDemandeAchatService {

    private final LigneDemandeAchatRepository ligneDemandeAchatRepository;

    public LigneDemandeAchatService(LigneDemandeAchatRepository ligneDemandeAchatRepository) {
        this.ligneDemandeAchatRepository = ligneDemandeAchatRepository;
    }
}
