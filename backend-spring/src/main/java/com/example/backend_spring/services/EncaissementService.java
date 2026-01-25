package com.example.backend_spring.services;

import com.example.backend_spring.models.Encaissement;
import com.example.backend_spring.models.FactureClient;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.repositories.EncaissementRepository;
import com.example.backend_spring.repositories.FactureClientRepository;
import com.example.backend_spring.repositories.JournalAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EncaissementService {
    private final EncaissementRepository encaissementRepository;
    private final JournalAuditRepository journalAuditRepository;
    private final FactureClientRepository factureClientRepository;
    private final BudgetService budgetService;

    public List<Encaissement> getAllEncaissements() {
        return encaissementRepository.findAll();
    }

    public Optional<Encaissement> getEncaissementById(int id) {
        return encaissementRepository.findById(id);
    }

    @Transactional
    public Encaissement saveEncaissement(Encaissement encaissement) {
        // Séparation des tâches : celui qui encaisse ne doit pas avoir validé la
        // facture ou l'avoir, ni avoir créé le client
        if (encaissement.getFactureClient() != null && encaissement.getUtilisateur() != null) {
            int utilisateurId = encaissement.getUtilisateur().getId();
            FactureClient facture = factureClientRepository.findById(encaissement.getFactureClient().getId())
                    .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

            // 1. Contrôle : Ne pas avoir validé le document
            List<JournalAudit> auditsVentes = journalAuditRepository.findByModuleAndReferenceObjetOrderByDateActionDesc(
                    "VENTES", facture.getReference());

            boolean aValideDocument = auditsVentes.stream()
                    .anyMatch(a -> ("VALIDATION_FACTURE".equalsIgnoreCase(a.getAction())
                            || "VALIDATION_AVOIR".equalsIgnoreCase(a.getAction()))
                            && a.getUtilisateur().getId() == utilisateurId);

            if (aValideDocument) {
                throw new RuntimeException(
                        "Séparation des tâches : L'utilisateur ayant validé le document (facture/avoir) ne peut pas procéder à son encaissement.");
            }

            // 2. Contrôle : Ne pas avoir créé le client
            List<JournalAudit> auditsClient = journalAuditRepository.findByModuleAndReferenceObjetOrderByDateActionDesc(
                    "CLIENT", String.valueOf(facture.getClient().getId()));

            boolean aCreeClient = auditsClient.stream()
                    .anyMatch(a -> "CRÉATION".equalsIgnoreCase(a.getAction())
                            && a.getUtilisateur().getId() == utilisateurId);

            if (aCreeClient) {
                throw new RuntimeException(
                        "Séparation des tâches : Le créateur du client ne peut pas procéder à l'encaissement de ses factures.");
            }
        }

        Encaissement saved = encaissementRepository.save(encaissement);

        // Mettre à jour le statut de la facture et le budget
        if (encaissement.getFactureClient() != null) {
            FactureClient facture = factureClientRepository.findById(encaissement.getFactureClient().getId())
                    .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
            facture.setStatut("payee");
            factureClientRepository.save(facture);

            // Recharger le budget du département qui a fait la vente
            if (facture.getCommandeClient() != null &&
                    facture.getCommandeClient().getUtilisateur() != null &&
                    facture.getCommandeClient().getUtilisateur().getDepartement() != null) {

                budgetService.rechargerBudget(
                        facture.getCommandeClient().getUtilisateur().getDepartement(),
                        facture.getMontantTotal());
            }
        }

        return saved;
    }

    @Transactional
    public void deleteEncaissement(int id) {
        encaissementRepository.deleteById(id);
    }
}
