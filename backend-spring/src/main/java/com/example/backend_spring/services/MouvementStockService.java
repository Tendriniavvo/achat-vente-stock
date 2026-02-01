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

    private void synchronizeHeaderWithLines(MouvementStock mouvement) {
        if (mouvement.getLignes() != null && !mouvement.getLignes().isEmpty()) {
            int total = 0;
            for (LigneMouvementStock ligne : mouvement.getLignes()) {
                total += ligne.getQuantite();
                ligne.setMouvement(mouvement);
                // Propagation de l'emplacement du header vers la ligne si celle-ci n'en a pas
                if (ligne.getEmplacement() == null && mouvement.getEmplacement() != null) {
                    ligne.setEmplacement(mouvement.getEmplacement());
                }
            }
            mouvement.setQuantite(total);

            // Si une seule ligne, on synchronise les champs redondants sur le mouvement
            if (mouvement.getLignes().size() == 1) {
                LigneMouvementStock unique = mouvement.getLignes().get(0);
                mouvement.setArticle(unique.getArticle());
                mouvement.setCout(unique.getCoutUnitaire());
                mouvement.setLot(unique.getLot());

                // Si la ligne n'a pas d'emplacement, on lui donne celui du mouvement
                if (unique.getEmplacement() == null) {
                    unique.setEmplacement(mouvement.getEmplacement());
                }
                // Si le mouvement n'a pas d'emplacement, on lui donne celui de la ligne
                if (mouvement.getEmplacement() == null) {
                    mouvement.setEmplacement(unique.getEmplacement());
                }
            } else {
                // Si plusieurs lignes, on vide les champs spécifiques à un article unique
                mouvement.setArticle(null);
                mouvement.setCout(null);
                mouvement.setLot(null);
                // On garde l'emplacement global si toutes les lignes ont le même ou si non
                // spécifié sur les lignes
            }
        }
    }

    @Transactional
    public MouvementStock saveMouvement(MouvementStock mouvement) {
        if (mouvement.getReference() == null || mouvement.getReference().isBlank()) {
            mouvement.setReference(generateReference());
        }

        synchronizeHeaderWithLines(mouvement);

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
            mouvement.setLignes(lignes);
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
        synchronizeHeaderWithLines(mouvement);
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
                        + ", deltaValeur=" + deltaValeur);
        journalAuditRepository.save(audit);

        return saved;
    }

    private BigDecimal appliquerEntree(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;

        // On fait une copie pour éviter de vider la liste si 'lignes' est la collection
        // gérée par JPA
        List<LigneMouvementStock> copy = new ArrayList<>(lignes);
        mouvement.getLignes().clear();
        mouvement.getLignes().addAll(copy);

        for (LigneMouvementStock ligne : copy) {
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
                    ligne.getCoutUnitaire()));
            appliquerVariationLot(ligne.getLot(), ligne.getQuantite());
        }
        return deltaValeur;
    }

    private BigDecimal appliquerSortie(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        List<LigneMouvementStock> expanded = expandLotsIfNeeded(lignes, true);

        // Mise à jour sécurisée de la collection pour JPA
        mouvement.getLignes().clear();
        mouvement.getLignes().addAll(expanded);

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
                    ligne.getCoutUnitaire()));
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

        mouvement.getLignes().clear();
        mouvement.getLignes().addAll(expanded);

        for (LigneMouvementStock ligne : expanded) {
            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException("Quantité transfert invalide");
            }
            Emplacement emplacementSource = resolveAndValidateEmplacement(mouvement, ligne);
            controlerLotEtTraçabilite(ligne.getArticle(), ligne.getLot(), true);

            deltaValeur = deltaValeur.add(appliquerVariationStock(ligne, mouvement.getDepot(), emplacementSource,
                    -ligne.getQuantite(), ligne.getCoutUnitaire()));

            Emplacement emplacementDest = mouvement.getEmplacementDestination();
            deltaValeur = deltaValeur.add(appliquerVariationStock(ligne, mouvement.getDepotDestination(),
                    emplacementDest, ligne.getQuantite(), ligne.getCoutUnitaire()));
        }
        return deltaValeur;
    }

    private BigDecimal appliquerAjustement(MouvementStock mouvement, List<LigneMouvementStock> lignes) {
        BigDecimal deltaValeur = BigDecimal.ZERO;
        List<LigneMouvementStock> expanded = expandLotsIfNeeded(lignes, false);

        mouvement.getLignes().clear();
        mouvement.getLignes().addAll(expanded);

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
                    ligne.getCoutUnitaire()));
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
            BigDecimal coutUnitaire) {
        if (ligne.getArticle() == null) {
            throw new IllegalArgumentException("Article obligatoire dans la ligne");
        }
        if (depot == null) {
            throw new IllegalArgumentException("Depot obligatoire pour mise à jour stock");
        }
        if (emplacement == null) {
            throw new IllegalArgumentException("Emplacement obligatoire pour mise à jour stock");
        }

        // Si c'est une sortie (variation négative), on doit potentiellement décomposer
        // sur plusieurs entrées
        if (variationQuantite < 0) {
            return consommerStock(ligne, depot, emplacement, -variationQuantite);
        }

        // Pour les entrées (variation positive), on cherche l'entrée existante avec ce
        // coût ou on en crée une
        BigDecimal cuToSearch = coutUnitaire != null ? coutUnitaire : BigDecimal.ZERO;

        Stock stock = stockRepository
                .findByArticleAndDepotAndEmplacementAndCoutUnitaire(ligne.getArticle(), depot, emplacement, cuToSearch)
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setArticle(ligne.getArticle());
                    s.setDepot(depot);
                    s.setEmplacement(emplacement);
                    s.setQuantite(0);
                    s.setCoutUnitaire(cuToSearch);
                    s.setValeur(BigDecimal.ZERO);
                    return s;
                });

        int ancienneQuantite = stock.getQuantite();
        BigDecimal ancienneValeur = stock.getValeur() == null ? BigDecimal.ZERO : stock.getValeur();

        int nouvelleQuantite = ancienneQuantite + variationQuantite;
        BigDecimal nouvelleValeur = ancienneValeur.add(cuToSearch.multiply(BigDecimal.valueOf(variationQuantite)));

        stock.setQuantite(nouvelleQuantite);
        stock.setValeur(nouvelleValeur);
        stock.setDateMaj(LocalDateTime.now());
        stockRepository.save(stock);

        return nouvelleValeur.subtract(ancienneValeur);
    }

    private BigDecimal consommerStock(LigneMouvementStock ligne, com.example.backend_spring.models.Depot depot,
            com.example.backend_spring.models.Emplacement emplacement, int quantiteASortir) {
        Article article = ligne.getArticle();
        List<Stock> stocksDisponibles = stockRepository.findByArticleId(article.getId()).stream()
                .filter(s -> s.getDepot() != null && s.getDepot().getId() == depot.getId())
                .filter(s -> {
                    if (emplacement == null) {
                        return s.getEmplacement() == null;
                    } else {
                        return s.getEmplacement() != null && s.getEmplacement().getId() == emplacement.getId();
                    }
                })
                .filter(s -> s.getQuantite() > 0)
                .collect(java.util.stream.Collectors.toList());

        if (stocksDisponibles.isEmpty()) {
            throw new IllegalStateException(
                    "Aucun stock disponible pour l'article " + article.getCode() + " au dépôt " + depot.getNom());
        }

        // Tri selon la méthode de valorisation
        String methode = article.getMethodeValorisation() != null ? article.getMethodeValorisation().toUpperCase()
                : "CUMP";
        if ("FIFO".equals(methode)) {
            stocksDisponibles
                    .sort(Comparator.comparing(s -> s.getDateMaj() != null ? s.getDateMaj() : LocalDateTime.MIN));
        } else if ("LIFO".equals(methode)) {
            stocksDisponibles.sort(Comparator
                    .comparing((Stock s) -> s.getDateMaj() != null ? s.getDateMaj() : LocalDateTime.MIN).reversed());
        }
        // Pour CUMP, on peut aussi trier par FIFO par défaut ou prorata (ici FIFO pour
        // simplifier la décomposition)

        int restant = quantiteASortir;
        BigDecimal valeurSortieTotale = BigDecimal.ZERO;

        for (Stock s : stocksDisponibles) {
            if (restant <= 0)
                break;

            int aPrendre = Math.min(s.getQuantite(), restant);
            BigDecimal valeurSortie = s.getCoutUnitaire().multiply(BigDecimal.valueOf(aPrendre));

            s.setQuantite(s.getQuantite() - aPrendre);
            s.setValeur(s.getValeur().subtract(valeurSortie));
            if (s.getValeur().compareTo(BigDecimal.ZERO) < 0)
                s.setValeur(BigDecimal.ZERO);
            s.setDateMaj(LocalDateTime.now());
            stockRepository.save(s);

            valeurSortieTotale = valeurSortieTotale.add(valeurSortie);
            restant -= aPrendre;
        }

        if (restant > 0) {
            throw new IllegalStateException(
                    "Stock insuffisant pour l'article " + article.getCode() + " (manque " + restant + ")");
        }

        return valeurSortieTotale.negate(); // Retourne la variation de valeur (négative)
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
