package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceptionService {
    private final ReceptionRepository receptionRepository;
    private final LigneReceptionRepository ligneReceptionRepository;
    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
    private final LigneBonCommandeRepository ligneBonCommandeRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;
    private final DepotRepository depotRepository;
    private final EmplacementRepository emplacementRepository;

    public List<Reception> getAllReceptions() {
        return receptionRepository.findAll();
    }

    public Optional<Reception> getReceptionById(int id) {
        return receptionRepository.findById(id);
    }

    public List<LigneReception> getLignesByReceptionId(int receptionId) {
        return ligneReceptionRepository.findByReceptionId(receptionId);
    }

    public Optional<Reception> getReceptionByReference(String reference) {
        return receptionRepository.findByReference(reference);
    }

    @Transactional
    public Reception enregistrerReception(int bcId, int utilisateurId, int depotId, List<Map<String, Object>> items) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(bcId)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));

        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Depot depot = depotRepository.findById(depotId)
                .orElseThrow(() -> new RuntimeException("Dépôt non trouvé"));

        // Vérifier les droits (Magasinier ou Admin)
        if (!hasRole(user, "Magasinier") && !hasRole(user, "Administrateur")) {
            throw new RuntimeException("Seul un Magasinier ou Admin peut enregistrer une réception");
        }

        // Créer la réception
        Reception reception = new Reception();
        reception.setBonCommande(bc);
        reception.setUtilisateur(user);
        reception.setDepot(depot);
        reception.setDateReception(LocalDateTime.now());
        reception.setReference("BR-" + System.currentTimeMillis());
        reception.setStatut("partielle");

        Reception savedReception = receptionRepository.save(reception);

        boolean allReceived = true;
        List<LigneBonCommande> bcLines = ligneBonCommandeRepository.findByBonCommandeId(bcId);

        for (Map<String, Object> item : items) {
            int articleId = (int) item.get("articleId");
            int quantiteRecue = (int) item.get("quantiteRecue");
            String numeroLot = (String) item.get("numeroLot");
            String ecart = (String) item.get("ecart");
            LocalDateTime dateExpiration = item.get("dateExpiration") != null
                    ? LocalDateTime.parse((String) item.get("dateExpiration"))
                    : null;
            Integer emplacementId = (Integer) item.get("emplacementId");

            if (quantiteRecue <= 0)
                continue;

            Article article = new Article();
            article.setId(articleId);

            // Gestion du Lot
            Lot lot = null;
            if (numeroLot != null && !numeroLot.isEmpty()) {
                Optional<Lot> existingLot = lotRepository.findByNumeroLot(numeroLot);
                if (existingLot.isPresent()) {
                    lot = existingLot.get();
                    // Vérifier si lot expiré
                    if (lot.getDateExpiration() != null && lot.getDateExpiration().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("Le lot " + numeroLot + " est expiré");
                    }
                    // Mettre à jour la quantité du lot existant si on reçoit à nouveau le même lot
                    lot.setQuantite((lot.getQuantite() != null ? lot.getQuantite() : 0) + quantiteRecue);
                    lot = lotRepository.save(lot);
                } else {
                    lot = new Lot();
                    lot.setNumeroLot(numeroLot);
                    lot.setArticle(article);
                    lot.setDateEntree(LocalDateTime.now());
                    lot.setDateExpiration(dateExpiration);
                    lot.setQuantite(quantiteRecue); // Initialiser la quantité du lot avec la quantité reçue
                    lot.setConforme(true);
                    lot = lotRepository.save(lot);
                }
            }

            // Créer LigneReception
            LigneReception lr = new LigneReception();
            lr.setReception(savedReception);
            lr.setArticle(article);
            lr.setQuantiteRecue(quantiteRecue);
            lr.setLot(lot);
            lr.setEcart(ecart);
            ligneReceptionRepository.save(lr);

            // Trouver la ligne de BC correspondante pour le coût
            Optional<LigneBonCommande> matchingBcLine = bcLines.stream()
                    .filter(l -> l.getArticle().getId() == articleId)
                    .findFirst();

            // Mouvement de stock
            MouvementStock mouvement = new MouvementStock();
            mouvement.setType("entree");
            mouvement.setArticle(article);
            mouvement.setQuantite(quantiteRecue);
            mouvement.setDepot(depot);
            if (emplacementId != null) {
                Emplacement emp = emplacementRepository.findById(emplacementId).orElse(null);
                mouvement.setEmplacement(emp);
            }
            mouvement.setLot(lot);
            mouvement.setReferenceDocument(savedReception.getReference());
            mouvement.setUtilisateur(user);
            mouvement.setDateMouvement(LocalDateTime.now());
            mouvement.setMotif("Réception BC " + bc.getReference());
            mouvement.setStatut("VALIDE");

            // Définir le coût basé sur le prix unitaire du BC
            if (matchingBcLine.isPresent()) {
                mouvement.setCout(matchingBcLine.get().getPrixUnitaire());
            }

            mouvementStockRepository.save(mouvement);

            // Mise à jour du Stock
            Stock stock = stockRepository.findByArticleIdAndDepotId(articleId, depotId)
                    .orElseGet(() -> {
                        Stock s = new Stock();
                        s.setArticle(article);
                        s.setDepot(depot);
                        s.setQuantite(0);
                        return s;
                    });
            stock.setQuantite(stock.getQuantite() + quantiteRecue);

            // Correction : Définir l'emplacement dans la table stocks si fourni
            if (emplacementId != null) {
                Emplacement emp = emplacementRepository.findById(emplacementId).orElse(null);
                stock.setEmplacement(emp);
            }

            stockRepository.save(stock);
        }

        // Vérifier si la réception est complète
        // On compare le total reçu par article vs total commandé
        for (LigneBonCommande lbc : bcLines) {
            int totalRecu = getTotalRecuForArticleInBc(bcId, lbc.getArticle().getId());
            if (totalRecu < lbc.getQuantite()) {
                allReceived = false;
                break;
            }
        }

        if (allReceived) {
            savedReception.setStatut("complete");
            bc.setStatut("receptionne");
        } else {
            bc.setStatut("reception_partielle");
        }

        receptionRepository.save(savedReception);
        bonCommandeFournisseurRepository.save(bc);

        // Audit
        journalAuditService.logAction(
                user,
                "RECEPTION_ARTICLES",
                "STOCKS",
                savedReception.getReference(),
                "Réception enregistrée pour le BC " + bc.getReference() + " par " + user.getNom());

        return savedReception;
    }

    private int getTotalRecuForArticleInBc(int bcId, int articleId) {
        List<Reception> receptions = receptionRepository.findByBonCommandeId(bcId);
        int total = 0;
        for (Reception r : receptions) {
            List<LigneReception> lignes = ligneReceptionRepository.findByReceptionId(r.getId());
            for (LigneReception lr : lignes) {
                if (lr.getArticle().getId() == articleId) {
                    total += lr.getQuantiteRecue();
                }
            }
        }
        return total;
    }

    private boolean hasRole(Utilisateur user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getNom().equalsIgnoreCase(roleName));
    }

    @Transactional
    public void deleteReception(int id) {
        receptionRepository.deleteById(id);
    }
}
