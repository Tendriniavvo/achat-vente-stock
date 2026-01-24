package com.example.backend_spring.services;

import com.example.backend_spring.models.Client;
import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.models.FactureClient;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.ClientRepository;
import com.example.backend_spring.repositories.CommandeClientRepository;
import com.example.backend_spring.repositories.FactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final FactureClientRepository factureClientRepository;
    private final JournalAuditService journalAuditService;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(int id) {
        return clientRepository.findById(id);
    }

    @Transactional
    public Client saveClient(Client client, Utilisateur utilisateur) {
        boolean isNew = client.getId() == 0;
        Client saved = clientRepository.save(client);
        
        String action = isNew ? "CRÉATION" : "MODIFICATION";
        String details = isNew ? "Création du client " + saved.getNom() : "Modification du client " + saved.getNom();
        
        journalAuditService.logAction(utilisateur, action, "CLIENT", String.valueOf(saved.getId()), details);
        
        return saved;
    }

    @Transactional
    public Client toggleStatus(int id, Utilisateur utilisateur) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        
        client.setActif(!client.isActif());
        Client saved = clientRepository.save(client);
        
        String action = saved.isActif() ? "ACTIVATION" : "DÉSACTIVATION";
        journalAuditService.logAction(utilisateur, action, "CLIENT", String.valueOf(saved.getId()), action + " du client " + saved.getNom());
        
        return saved;
    }

    public List<CommandeClient> getClientOrders(int id) {
        return commandeClientRepository.findByClientIdOrderByDateCommandeDesc(id);
    }

    public List<FactureClient> getClientInvoices(int id) {
        return factureClientRepository.findByClientIdOrderByDateFactureDesc(id);
    }

    public List<JournalAudit> getClientHistory(int id) {
        return journalAuditService.getAuditsByModuleAndReference("CLIENT", String.valueOf(id));
    }

    public Map<String, Object> getClientStats(int id) {
        List<CommandeClient> orders = commandeClientRepository.findByClientIdOrderByDateCommandeDesc(id);
        List<FactureClient> invoices = factureClientRepository.findByClientIdOrderByDateFactureDesc(id);
        
        long totalOrders = orders.size();
        double totalAmount = invoices.stream()
                .filter(f -> "payee".equals(f.getStatut()) || "partiel".equals(f.getStatut()))
                .mapToDouble(f -> f.getMontantTotal() != null ? f.getMontantTotal().doubleValue() : 0.0)
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalAmount", totalAmount);
        
        return stats;
    }

    @Transactional
    public void deleteClient(int id) {
        clientRepository.deleteById(id);
    }
}
