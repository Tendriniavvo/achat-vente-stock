package com.example.backend_spring.services;

import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.models.Livraison;
import com.example.backend_spring.models.LigneLivraison;
import com.example.backend_spring.models.MouvementStock;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.CommandeClientRepository;
import com.example.backend_spring.repositories.LivraisonRepository;
import com.example.backend_spring.repositories.LigneLivraisonRepository;
import com.example.backend_spring.repositories.MouvementStockRepository;
import com.example.backend_spring.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;
    private final LivraisonRepository livraisonRepository;
    private final LigneLivraisonRepository ligneLivraisonRepository;

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

    @Transactional
    public CommandeClient preparerLivraison(int commandeId, int utilisateurId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si la commande est réservée ou confirmée
        if (!"reservee".equalsIgnoreCase(commande.getStatut()) && !"confirmee".equalsIgnoreCase(commande.getStatut())) {
            throw new RuntimeException(
                    "Seule une commande réservée ou confirmée peut être préparée pour la livraison.");
        }

        // Trouver les mouvements de réservation
        List<MouvementStock> reservations = mouvementStockRepository.findByReferenceDocumentAndType(
                commande.getReference(),
                "reservation");

        if (reservations.isEmpty()) {
            throw new RuntimeException("Aucune réservation de stock trouvée pour cette commande.");
        }

        // 1. Créer le Bon de Livraison
        Livraison livraison = new Livraison();
        livraison.setReference("LIV-" + System.currentTimeMillis());
        livraison.setCommandeClient(commande);
        livraison.setDateLivraison(LocalDateTime.now());
        livraison.setStatut("preparation");
        livraison.setUtilisateur(utilisateur);

        // Récupérer le dépôt depuis la première réservation si disponible
        if (!reservations.isEmpty()) {
            MouvementStock firstRes = reservations.get(0);
            if (firstRes.getEmplacement() != null && firstRes.getEmplacement().getDepot() != null) {
                livraison.setDepot(firstRes.getEmplacement().getDepot());
            } else if (firstRes.getDepot() != null) {
                livraison.setDepot(firstRes.getDepot());
            }
        }
        Livraison savedLivraison = livraisonRepository.save(livraison);

        // 2. Créer les lignes de livraison à partir des réservations
        for (MouvementStock res : reservations) {
            LigneLivraison ll = new LigneLivraison();
            ll.setLivraison(savedLivraison);
            ll.setArticle(res.getArticle());
            ll.setQuantiteLivree(res.getQuantite());
            ll.setLot(res.getLot());
            ll.setEmplacement(res.getEmplacement());
            ligneLivraisonRepository.save(ll);
        }

        // 3. Mettre à jour le statut de la commande
        commande.setStatut("livraison_en_cours");
        CommandeClient saved = commandeClientRepository.save(commande);

        journalAuditService.logAction(
                utilisateur,
                "PREPARATION_LIVRAISON",
                "VENTES",
                saved.getReference(),
                "Bon de livraison " + savedLivraison.getReference() + " créé pour la commande " + saved.getReference());

        return saved;
    }

    @Transactional
    public CommandeClient updateStatut(int id, String statut) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setStatut(statut);
        return commandeClientRepository.save(commande);
    }
}
