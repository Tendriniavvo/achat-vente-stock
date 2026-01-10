package com.example.backend_spring.service.vente;

import com.example.backend_spring.repository.vente.CommandeClientRepository;
import org.springframework.stereotype.Service;

@Service
public class CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;

    public CommandeClientService(CommandeClientRepository commandeClientRepository) {
        this.commandeClientRepository = commandeClientRepository;
    }
}
