package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.DemandeAchatRepository;
import org.springframework.stereotype.Service;

@Service
public class DemandeAchatService {

    private final DemandeAchatRepository demandeAchatRepository;

    public DemandeAchatService(DemandeAchatRepository demandeAchatRepository) {
        this.demandeAchatRepository = demandeAchatRepository;
    }
}
