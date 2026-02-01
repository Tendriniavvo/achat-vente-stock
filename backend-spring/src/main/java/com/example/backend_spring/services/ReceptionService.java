package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ArticleRepository articleRepository;

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
            int articleId = getIntFromMap(item, "articleId");
            int quantiteRecue = getIntFromMap(item, "quantiteRecue");

            String numeroLot = getStringFromMap(item, "numeroLot");
            String ecart = getStringFromMap(item, "ecart");
            String dateExpStr = getStringFromMap(item, "dateExpiration");
            LocalDateTime dateExpiration = parseDate(dateExpStr);
            Integer emplacementId = getIntegerFromMap(item, "emplacementId");

            if (quantiteRecue <= 0)
                continue;

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Article non trouvé: " + articleId));

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

            // IMPORTANT: Synchroniser avec les lignes pour que appliquerVariationStock
            // puisse récupérer le coût depuis le mouvement (header)
            // On initialise explicitement la liste au cas où @NoArgsConstructor ou Lombok
            // la mettrait à null
            if (mouvement.getLignes() == null) {
                mouvement.setLignes(new ArrayList<>());
            }
            LigneMouvementStock lm = new LigneMouvementStock();
            lm.setMouvement(mouvement);
            lm.setArticle(article);
            lm.setQuantite(quantiteRecue);
            lm.setCoutUnitaire(mouvement.getCout());
            lm.setLot(lot);
            lm.setEmplacement(mouvement.getEmplacement());
            mouvement.getLignes().add(lm);

            mouvementStockRepository.save(mouvement);

            // Mise à jour du Stock via MouvementStockService pour garantir la cohérence du
            // coût
            // On délègue maintenant la mise à jour du stock à
            // MouvementStockService.validerMouvement
            // mais comme on est déjà en statut VALIDE et qu'on veut forcer le prix du
            // mouvement :
            // On utilise directement la logique de mise à jour de stock avec le coût du
            // mouvement.

            BigDecimal coutUnitaire = mouvement.getCout();
            if (coutUnitaire == null || coutUnitaire.compareTo(BigDecimal.ZERO) <= 0) {
                if (article.getPrixAchat() != null) {
                    coutUnitaire = article.getPrixAchat();
                } else {
                    coutUnitaire = BigDecimal.ZERO;
                }
            }

            final BigDecimal finalCout = coutUnitaire;
            Emplacement finalEmplacement = mouvement.getEmplacement();

            Stock stock = stockRepository
                    .findByArticleAndDepotAndEmplacementAndCoutUnitaire(article, depot, finalEmplacement, finalCout)
                    .orElseGet(() -> {
                        Stock s = new Stock();
                        s.setArticle(article);
                        s.setDepot(depot);
                        s.setEmplacement(finalEmplacement);
                        s.setQuantite(0);
                        s.setCoutUnitaire(finalCout);
                        s.setValeur(BigDecimal.ZERO);
                        return s;
                    });

            int ancienneQuantite = stock.getQuantite();
            BigDecimal ancienneValeur = stock.getValeur() != null ? stock.getValeur() : BigDecimal.ZERO;

            stock.setQuantite(ancienneQuantite + quantiteRecue);
            stock.setValeur(ancienneValeur.add(finalCout.multiply(BigDecimal.valueOf(quantiteRecue))));
            stock.setDateMaj(LocalDateTime.now());

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

    private int getIntFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Double)
            return ((Double) value).intValue();
        if (value instanceof String && !((String) value).isBlank())
            return Integer.parseInt((String) value);
        if (value instanceof Number)
            return ((Number) value).intValue();
        return 0;
    }

    private Integer getIntegerFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Double)
            return ((Double) value).intValue();
        if (value instanceof String && !((String) value).isBlank())
            return Integer.parseInt((String) value);
        if (value instanceof Number)
            return ((Number) value).intValue();
        return null;
    }

    private String getStringFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        return String.valueOf(value);
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            return null;
        try {
            if (dateStr.contains("T")) {
                return LocalDateTime.parse(dateStr);
            } else {
                return java.time.LocalDate.parse(dateStr).atStartOfDay();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
