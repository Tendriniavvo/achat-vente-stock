package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivraisonService {
    private final LivraisonRepository livraisonRepository;
    private final LigneLivraisonRepository ligneLivraisonRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final StockRepository stockRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;

    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }

    public Optional<Livraison> getLivraisonById(int id) {
        return livraisonRepository.findById(id);
    }

    public List<LigneLivraison> getLignesByLivraisonId(int livraisonId) {
        return ligneLivraisonRepository.findByLivraisonId(livraisonId);
    }

    @Transactional
    public Livraison expedierLivraison(int livraisonId, int utilisateurId) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));

        if (!"preparation".equalsIgnoreCase(livraison.getStatut())) {
            throw new RuntimeException("Seule une livraison en préparation peut être expédiée.");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<LigneLivraison> lignes = ligneLivraisonRepository.findByLivraisonId(livraisonId);
        CommandeClient commande = livraison.getCommandeClient();

        for (LigneLivraison ligne : lignes) {
            // 1. Trouver et supprimer la réservation
            List<MouvementStock> reservations = mouvementStockRepository.findByReferenceDocumentAndType(
                    commande.getReference(), "reservation");

            MouvementStock reservation = reservations.stream()
                    .filter(r -> r.getArticle().getId() == ligne.getArticle().getId() &&
                            (r.getLot() == null
                                    || (ligne.getLot() != null && r.getLot().getId() == ligne.getLot().getId()))
                            &&
                            (r.getEmplacement() == null || (ligne.getEmplacement() != null
                                    && r.getEmplacement().getId() == ligne.getEmplacement().getId())))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Réservation non trouvée pour l'article " + ligne.getArticle().getNom()));

            mouvementStockRepository.delete(reservation);

            // 2. Créer le mouvement de sortie
            MouvementStock sortie = new MouvementStock();
            sortie.setType("sortie");
            sortie.setArticle(ligne.getArticle());
            sortie.setQuantite(ligne.getQuantiteLivree());
            // On récupère le lot et l'emplacement depuis la réservation pour être sûr
            sortie.setLot(reservation.getLot());
            sortie.setEmplacement(reservation.getEmplacement());
            sortie.setDepot(reservation.getDepot() != null ? reservation.getDepot() : livraison.getDepot());
            sortie.setReferenceDocument(livraison.getReference());
            sortie.setUtilisateur(utilisateur);
            sortie.setDateMouvement(LocalDateTime.now());
            sortie.setCout(reservation.getCout()); // On garde le coût de la réservation (fixé dans DevisClientService)
            sortie.setMotif("Expédition livraison " + livraison.getReference());

            mouvementStockRepository.save(sortie);

            // 3. Le stock physique a déjà été diminué lors de la réservation
            // (dans DevisClientService.reserverStock), donc on ne le diminue pas une
            // seconde fois ici.
        }

        // 4. Mettre à jour les statuts
        livraison.setStatut("expediee");
        livraison.setDateLivraison(LocalDateTime.now()); // Date effective d'expédition
        livraisonRepository.save(livraison);

        commande.setStatut("livree");
        commandeClientRepository.save(commande);

        // 5. Audit
        journalAuditService.logAction(
                utilisateur,
                "EXPEDITION_LIVRAISON",
                "VENTES",
                livraison.getReference(),
                "Livraison " + livraison.getReference() + " expédiée pour la commande " + commande.getReference());

        return livraison;
    }

    @Transactional
    public Livraison saveLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    @Transactional
    public void deleteLivraison(int id) {
        livraisonRepository.deleteById(id);
    }
}
