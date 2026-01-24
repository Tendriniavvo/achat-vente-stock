package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Client;
import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.models.FactureClient;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.services.ClientService;
import com.example.backend_spring.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;
    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable int id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders")
    public List<CommandeClient> getClientOrders(@PathVariable int id) {
        return clientService.getClientOrders(id);
    }

    @GetMapping("/{id}/invoices")
    public List<FactureClient> getClientInvoices(@PathVariable int id) {
        return clientService.getClientInvoices(id);
    }

    @GetMapping("/{id}/history")
    public List<JournalAudit> getClientHistory(@PathVariable int id) {
        return clientService.getClientHistory(id);
    }

    @GetMapping("/{id}/stats")
    public Map<String, Object> getClientStats(@PathVariable int id) {
        return clientService.getClientStats(id);
    }

    @PostMapping
    public ResponseEntity<?> saveClient(@RequestBody Client client, @RequestParam int utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> ResponseEntity.ok(clientService.saveClient(client, u)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable int id, @RequestParam int utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> ResponseEntity.ok(clientService.toggleStatus(id, u)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable int id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
