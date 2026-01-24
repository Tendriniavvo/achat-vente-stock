package com.example.backend_spring.services;

import com.example.backend_spring.models.BonCommandeFournisseur;
import com.example.backend_spring.models.Fournisseur;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.BonCommandeFournisseurRepository;
import com.example.backend_spring.repositories.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final BonCommandeFournisseurRepository bonCommandeRepository;
    private final JournalAuditService journalAuditService;

    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurRepository.findAll();
    }

    public Optional<Fournisseur> getFournisseurById(int id) {
        return fournisseurRepository.findById(id);
    }

    @Transactional
    public Fournisseur saveFournisseur(Fournisseur fournisseur, Utilisateur utilisateur) {
        boolean isNew = fournisseur.getId() == 0;
        Fournisseur saved = fournisseurRepository.save(fournisseur);

        String action = isNew ? "CRÉATION" : "MODIFICATION";
        String details = isNew ? "Création du fournisseur " + saved.getNom()
                : "Modification du fournisseur " + saved.getNom();

        journalAuditService.logAction(utilisateur, action, "FOURNISSEUR", String.valueOf(saved.getId()), details);

        return saved;
    }

    @Transactional
    public Fournisseur toggleStatus(int id, Utilisateur utilisateur) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

        fournisseur.setActif(!fournisseur.isActif());
        Fournisseur saved = fournisseurRepository.save(fournisseur);

        String action = saved.isActif() ? "ACTIVATION" : "DÉSACTIVATION";
        journalAuditService.logAction(utilisateur, action, "FOURNISSEUR", String.valueOf(saved.getId()),
                action + " du fournisseur " + saved.getNom());

        return saved;
    }

    public List<BonCommandeFournisseur> getFournisseurOrders(int id) {
        return bonCommandeRepository.findByFournisseur_IdOrderByDateCommandeDesc(id);
    }

    public List<JournalAudit> getFournisseurHistory(int id) {
        return journalAuditService.getAuditsByModuleAndReference("FOURNISSEUR", String.valueOf(id));
    }

    public Map<String, Object> getFournisseurStats(int id) {
        List<BonCommandeFournisseur> orders = bonCommandeRepository.findByFournisseur_IdOrderByDateCommandeDesc(id);

        long totalOrders = orders.size();
        double totalAmount = orders.stream()
                .filter(o -> !"annule".equals(o.getStatut()))
                .mapToDouble(o -> o.getMontantTotal() != null ? o.getMontantTotal().doubleValue() : 0.0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalAmount", totalAmount);

        // Indicateur stratégique : plus de 5 commandes ou plus de 10M MGA
        boolean isStrategic = totalOrders >= 5 || totalAmount >= 10000000;
        // Indicateur à risque : aucune commande depuis 6 mois si déjà client, ou statut
        // inactif
        stats.put("isStrategic", isStrategic);

        return stats;
    }

    @Transactional
    public void deleteFournisseur(int id) {
        fournisseurRepository.deleteById(id);
    }
}
