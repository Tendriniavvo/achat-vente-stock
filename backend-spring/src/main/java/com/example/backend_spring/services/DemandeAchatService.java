package com.example.backend_spring.services;

import com.example.backend_spring.models.DemandeAchat;
import com.example.backend_spring.repositories.DemandeAchatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DemandeAchatService {
    private final DemandeAchatRepository demandeAchatRepository;

    public List<DemandeAchat> getAllDemandes() {
        return demandeAchatRepository.findAll();
    }

    public Optional<DemandeAchat> getDemandeById(int id) {
        return demandeAchatRepository.findById(id);
    }

    public Optional<DemandeAchat> getDemandeByReference(String reference) {
        return demandeAchatRepository.findByReference(reference);
    }

    @Transactional
    public DemandeAchat saveDemande(DemandeAchat demande) {
        return demandeAchatRepository.save(demande);
    }

    @Transactional
    public void deleteDemande(int id) {
        demandeAchatRepository.deleteById(id);
    }
}
