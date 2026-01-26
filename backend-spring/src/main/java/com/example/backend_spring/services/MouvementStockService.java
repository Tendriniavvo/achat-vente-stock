package com.example.backend_spring.services;

import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.LigneMouvementStock;
import com.example.backend_spring.models.Lot;
import com.example.backend_spring.models.MouvementStock;
import com.example.backend_spring.models.Stock;
import com.example.backend_spring.repositories.MouvementStockRepository;
import com.example.backend_spring.repositories.StockRepository;
import com.example.backend_spring.repositories.JournalAuditRepository;
import com.example.backend_spring.repositories.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;
    private final JournalAuditRepository journalAuditRepository;

    public List<MouvementStock> getAllMouvements() {
        return mouvementStockRepository.findAll();
    }

    public Optional<MouvementStock> getMouvementById(int id) {
        return mouvementStockRepository.findByIdWithDetails(id);
    }

    @Transactional
    public MouvementStock saveMouvement(MouvementStock mouvement) {
        if (mouvement.getReference() == null || mouvement.getReference().isBlank()) {
            mouvement.setReference(generateReference());
        }

        if (mouvement.getQuantite() == null && mouvement.getLignes() != null && !mouvement.getLignes().isEmpty()) {
            int total = 0;
            for (LigneMouvementStock ligne : mouvement.getLignes()) {
                total += ligne.getQuantite();
            }
            mouvement.setQuantite(total);
        }

        if (mouvement.getLignes() != null) {
            for (LigneMouvementStock ligne : mouvement.getLignes()) {
                ligne.setMouvement(mouvement);
            }
        }
        return mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public MouvementStock validerMouvement(int id) {
        MouvementStock mouvement = mouvementStockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MouvementStock introuvable"));

        if ("VALIDE".equalsIgnoreCase(mouvement.getStatut())) {
            return mouvement;
        }
        if ("ANNULE".equalsIgnoreCase(mouvement.getStatut())) {
            throw new IllegalStateException("Impossible de valider un mouvement annulé");
        }

        List<LigneMouvementStock> lignes = mouvement.getLignes();
        if (lignes == null || lignes.isEmpty()) {
            lignes = new ArrayList<>();
            if (mouvement.getArticle() == null || mouvement.getQuantite() == null) {
                throw new IllegalArgumentException("Le mouvement doit avoir des lignes ou (article + quantite)");
            }
            LigneMouvementStock legacy = new LigneMouvementStock();
            legacy.setMouvement(mouvement);
            legacy.setArticle(mouvement.getArticle());
            legacy.setQuantite(mouvement.getQuantite());
            legacy.setCoutUnitaire(mouvement.getCout());
            legacy.setLot(mouvement.getLot());
            legacy.setEmplacement(mouvement.getEmplacement());
            lignes.add(legacy);

            // Important: ne pas remplacer la collection (orphanRemoval=true)
            // On alimente la collection existante en place.
            replaceLignesInPlace(mouvement, lignes);
        }

        BigDecimal deltaValeur = BigDecimal.ZERO;
        int deltaQuantite = 0;

        String type = mouvement.getType() == null ? "" : mouvement.getType().trim().toUpperCase();
        switch (type) {
            case "ENTREE" -> {
                deltaValeur = deltaValeur.add(appliquerEntree(mouvement, lignes));
                for (LigneMouvementStock l : mouvement.getLignes()) {
                    deltaQuantite += l.getQuantite();
                }
            }
            case "SORTIE" -> {
                deltaValeur = deltaValeur.add(appliquerSortie(mouvement, lignes));
                for (LigneMouvementStock l : mouvement.getLignes()) {
                    deltaQuantite -= l.getQuantite();
                }
            }
            case "TRANSFERT" -> {
                deltaValeur = deltaValeur.add(appliquerTransfert(mouvement, lignes));
            }
            case "AJUSTEMENT" -> {
                deltaValeur = deltaValeur.add(appliquerAjustement(mouvement, lignes));
                for (LigneMouvementStock l : mouvement.getLignes()) {
                    deltaQuantite += l.getQuantite();
                }
            }
            default -> throw new IllegalArgumentException("Type de mouvement non supporté: " + mouvement.getType());
        }

        mouvement.setStatut("VALIDE");
        MouvementStock saved = mouvementStockRepository.save(mouvement);

        JournalAudit audit = new JournalAudit();
        audit.setUtilisateur(mouvement.getUtilisateur());
        audit.setAction("VALIDATION_MOUVEMENT");
        audit.setModule("STOCK");
        audit.setReferenceObjet(mouvement.getReference());
        audit.setDetails(
                "Validation mouvement id=" + mouvement.getId()
                        + ", type=" + mouvement.getType()
                        + ", deltaQuantite=" + deltaQuantite
                        + ", deltaValeur=" + deltaValeur
        );
        journalAuditRepository.save(audit);

        return saved;
    }

    private BigDecimal appliquerEntree(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        for (LigneMouvementStock ligne : lignes) {
            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException("Quantité entrée invalide");
            }
            if (ligne.getCoutUnitaire() == null) {
                throw new IllegalArgumentException("Coût unitaire obligatoire sur une entrée");
            }
            Emplacement emplacement = resolveAndValidateEmplacement(mouvement, ligne);
            controlerLotEtTraçabilite(ligne.getArticle(), ligne.getLot(), true);
            deltaValeur = deltaValeur.add(appliquerVariationStock(
                    ligne,
                    mouvement.getDepot(),
                    emplacement,
                    ligne.getQuantite(),
                    ligne.getCoutUnitaire()
            ));
            appliquerVariationLot(ligne.getLot(), ligne.getQuantite());
        }
        return deltaValeur;
    }

    private BigDecimal appliquerSortie(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        List<LigneMouvementStock> expanded = expandLotsIfNeeded(lignes, true);
        replaceLignesInPlace(mouvement, expanded);
        for (LigneMouvementStock ligne : expanded) {
            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException("Quantité sortie invalide");
            }
            Emplacement emplacement = resolveAndValidateEmplacement(mouvement, ligne);
            controlerLotEtTraçabilite(ligne.getArticle(), ligne.getLot(), true);
            deltaValeur = deltaValeur.add(appliquerVariationStock(
                    ligne,
                    mouvement.getDepot(),
                    emplacement,
                    -ligne.getQuantite(),
                    ligne.getCoutUnitaire()
            ));
            appliquerVariationLot(ligne.getLot(), -ligne.getQuantite());
        }
        return deltaValeur;
    }

    private BigDecimal appliquerTransfert(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        if (mouvement.getDepotDestination() == null) {
            throw new IllegalArgumentException("Depot destination obligatoire pour un transfert");
        }
        if (mouvement.getEmplacementDestination() == null) {
            throw new IllegalArgumentException("Emplacement destination obligatoire pour un transfert");
        }
        List<LigneMouvementStock> expanded = expandLotsIfNeeded(lignes, true);
        replaceLignesInPlace(mouvement, expanded);
        for (LigneMouvementStock ligne : expanded) {
            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException("Quantité transfert invalide");
            }
            Emplacement emplacementSource = resolveAndValidateEmplacement(mouvement, ligne);
            controlerLotEtTraçabilite(ligne.getArticle(), ligne.getLot(), true);

            deltaValeur = deltaValeur.add(appliquerVariationStock(ligne, mouvement.getDepot(), emplacementSource, -ligne.getQuantite(), ligne.getCoutUnitaire()));

            Emplacement emplacementDest = mouvement.getEmplacementDestination();
            deltaValeur = deltaValeur.add(appliquerVariationStock(ligne, mouvement.getDepotDestination(), emplacementDest, ligne.getQuantite(), ligne.getCoutUnitaire()));
        }
        return deltaValeur;
    }

    private BigDecimal appliquerAjustement(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        List<LigneMouvementStock> expanded = expandLotsIfNeeded(lignes, false);
        replaceLignesInPlace(mouvement, expanded);
        for (LigneMouvementStock ligne : expanded) {
            if (ligne.getQuantite() == 0) {
                continue;
            }
            Emplacement emplacement = resolveAndValidateEmplacement(mouvement, ligne);
            boolean requireLot = ligne.getQuantite() < 0;
            controlerLotEtTraçabilite(ligne.getArticle(), ligne.getLot(), requireLot);
            deltaValeur = deltaValeur.add(appliquerVariationStock(
                    ligne,
                    mouvement.getDepot(),
                    emplacement,
                    ligne.getQuantite(),
                    ligne.getCoutUnitaire()
            ));
            appliquerVariationLot(ligne.getLot(), ligne.getQuantite());
        }
        return deltaValeur;
    }

    private Emplacement resolveAndValidateEmplacement(MouvementStock mouvement, LigneMouvementStock ligne) {
        Emplacement emplacement = ligne.getEmplacement() != null ? ligne.getEmplacement() : mouvement.getEmplacement();
        if (emplacement == null) {
            throw new IllegalArgumentException("Emplacement obligatoire (Option A)");
        }
        return emplacement;
    }

    private BigDecimal appliquerVariationStock(
            LigneMouvementStock ligne,
            com.example.backend_spring.models.Depot depot,
            com.example.backend_spring.models.Emplacement emplacement,
            int variationQuantite,
            BigDecimal coutUnitaire
    ) {
        if (ligne.getArticle() == null) {
            throw new IllegalArgumentException("Article obligatoire dans la ligne");
        }
        if (depot == null) {
            throw new IllegalArgumentException("Depot obligatoire pour mise à jour stock");
        }
        if (emplacement == null) {
            throw new IllegalArgumentException("Emplacement obligatoire pour mise à jour stock");
        }

        Stock stock = stockRepository.findByArticleAndDepotAndEmplacement(ligne.getArticle(), depot, emplacement)
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setArticle(ligne.getArticle());
                    s.setDepot(depot);
                    s.setEmplacement(emplacement);
                    s.setQuantite(0);
                    s.setValeur(BigDecimal.ZERO);
                    return s;
                });

        int ancienneQuantite = stock.getQuantite();
        BigDecimal ancienneValeur = stock.getValeur() == null ? BigDecimal.ZERO : stock.getValeur();

        int nouvelleQuantite = ancienneQuantite + variationQuantite;
        if (nouvelleQuantite < 0) {
            throw new IllegalStateException("Stock insuffisant pour l'article " + ligne.getArticle().getCode());
        }

        BigDecimal nouvelleValeur = ancienneValeur;
        if (variationQuantite > 0) {
            BigDecimal cu = coutUnitaire == null ? BigDecimal.ZERO : coutUnitaire;
            nouvelleValeur = ancienneValeur.add(cu.multiply(BigDecimal.valueOf(variationQuantite)));
        } else if (variationQuantite < 0) {
            int qtySortie = -variationQuantite;
            BigDecimal cu;
            if (coutUnitaire != null) {
                cu = coutUnitaire;
            } else {
                String methode = ligne.getArticle() == null || ligne.getArticle().getMethodeValorisation() == null
                        ? "CUMP"
                        : ligne.getArticle().getMethodeValorisation().trim().toUpperCase();
                if (ancienneQuantite > 0) {
                    cu = ancienneValeur.divide(BigDecimal.valueOf(ancienneQuantite), 6, RoundingMode.HALF_UP);
                } else {
                    cu = BigDecimal.ZERO;
                }
                if (!"CUMP".equalsIgnoreCase(methode) && !"FIFO".equalsIgnoreCase(methode)) {
                    methode = "CUMP";
                }
            }
            nouvelleValeur = ancienneValeur.subtract(cu.multiply(BigDecimal.valueOf(qtySortie)));
        }

        if (nouvelleValeur.compareTo(BigDecimal.ZERO) < 0) {
            nouvelleValeur = BigDecimal.ZERO;
        }

        stock.setQuantite(nouvelleQuantite);
        stock.setValeur(nouvelleValeur);
        stock.setDateMaj(LocalDateTime.now());
        stockRepository.save(stock);

        return nouvelleValeur.subtract(ancienneValeur);
    }

    private void controlerLotEtTraçabilite(Article article, Lot lot, boolean lotObligatoire) {
        if (article != null && article.isTraceableLot() && lotObligatoire && lot == null) {
            throw new IllegalArgumentException("Lot obligatoire pour l'article traçable: " + article.getCode());
        }
        if (lot == null) {
            return;
        }
        if (!lot.isConforme()) {
            throw new IllegalStateException("Lot non conforme: " + lot.getNumeroLot());
        }
        if (lot.getDateExpiration() != null && lot.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Lot expiré: " + lot.getNumeroLot());
        }
    }

    private void replaceLignesInPlace(MouvementStock mouvement, List<LigneMouvementStock> nouvellesLignes) {
        if (mouvement.getLignes() == null) {
            mouvement.setLignes(new ArrayList<>());
        }
        mouvement.getLignes().clear();
        if (nouvellesLignes == null) {
            return;
        }
        for (LigneMouvementStock l : nouvellesLignes) {
            if (l == null) {
                continue;
            }
            l.setMouvement(mouvement);
            mouvement.getLignes().add(l);
        }
    }

    private List<LigneMouvementStock> expandLotsIfNeeded(List<LigneMouvementStock> lignes, boolean requireLotAlways) {
        List<LigneMouvementStock> out = new ArrayList<>();
        for (LigneMouvementStock ligne : lignes) {
            if (ligne == null) {
                continue;
            }
            Article article = ligne.getArticle();
            if (article == null || !article.isTraceableLot() || ligne.getLot() != null) {
                out.add(ligne);
                continue;
            }

            boolean requireLot = requireLotAlways || ligne.getQuantite() < 0;
            if (!requireLot) {
                out.add(ligne);
                continue;
            }

            int qte = Math.abs(ligne.getQuantite());
            if (qte == 0) {
                continue;
            }

            List<Lot> lots = lotRepository.findAvailableLotsByArticleId(article.getId());
            LocalDateTime now = LocalDateTime.now();
            lots = (lots == null ? new ArrayList<>() : new ArrayList<>(lots));
            lots.removeIf(l -> l == null || (l.getDateExpiration() != null && l.getDateExpiration().isBefore(now)));
            lots.sort(lotComparatorForStrategy(article.getStockStrategy()));

            int remaining = qte;
            for (Lot lot : lots) {
                if (remaining <= 0) {
                    break;
                }
                int dispo = lot.getQuantite() == null ? 0 : lot.getQuantite();
                if (dispo <= 0) {
                    continue;
                }
                int take = Math.min(dispo, remaining);
                LigneMouvementStock split = new LigneMouvementStock();
                split.setMouvement(ligne.getMouvement());
                split.setArticle(ligne.getArticle());
                split.setEmplacement(ligne.getEmplacement());
                split.setCoutUnitaire(ligne.getCoutUnitaire());
                split.setLot(lot);
                split.setQuantite(ligne.getQuantite() < 0 ? -take : take);
                out.add(split);
                remaining -= take;
            }

            if (remaining > 0) {
                throw new IllegalStateException("Stock lot insuffisant pour l'article " + article.getCode());
            }
        }
        return out;
    }

    private Comparator<Lot> lotComparatorForStrategy(String strategy) {
        String s = strategy == null ? "FEFO" : strategy.trim().toUpperCase();
        return switch (s) {
            case "FIFO" -> Comparator.comparing(Lot::getDateEntree, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Lot::getId);
            case "LIFO" -> Comparator.comparing(Lot::getDateEntree, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(Lot::getId, Comparator.reverseOrder());
            default -> Comparator.comparing(Lot::getDateExpiration, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Lot::getDateEntree, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Lot::getId);
        };
    }

    private void appliquerVariationLot(Lot lot, int variationQuantite) {
        if (lot == null) {
            return;
        }
        Integer q = lot.getQuantite();
        int ancienne = q == null ? 0 : q;
        int nouvelle = ancienne + variationQuantite;
        if (nouvelle < 0) {
            throw new IllegalStateException("Quantité lot insuffisante: " + lot.getNumeroLot());
        }
        lot.setQuantite(nouvelle);
        lotRepository.save(lot);
    }

    private String generateReference() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int i = 0; i < 10; i++) {
            int rnd = ThreadLocalRandom.current().nextInt(0, 1_000_000);
            String ref = "MS-" + datePart + "-" + String.format("%06d", rnd);
            if (mouvementStockRepository.findByReference(ref).isEmpty()) {
                return ref;
            }
        }
        return "MS-" + datePart + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public void deleteMouvement(int id) {
        mouvementStockRepository.deleteById(id);
    }
}
