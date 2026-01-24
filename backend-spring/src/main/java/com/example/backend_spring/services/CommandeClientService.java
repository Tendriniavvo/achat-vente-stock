package com.example.backend_spring.services;

import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.repositories.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;

    public List<CommandeClient> getAllCommandes() {
        return commandeClientRepository.findAll();
    }

    public Optional<CommandeClient> getCommandeById(int id) {
        return commandeClientRepository.findById(id);
    }

    public Optional<CommandeClient> getCommandeByReference(String reference) {
        return commandeClientRepository.findByReference(reference);
    }

    @Transactional
    public CommandeClient saveCommande(CommandeClient commande) {
        return commandeClientRepository.save(commande);
    }

    @Transactional
    public void deleteCommande(int id) {
        commandeClientRepository.deleteById(id);
    }
}
