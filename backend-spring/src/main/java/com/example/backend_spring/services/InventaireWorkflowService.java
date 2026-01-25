package com.example.backend_spring.services;

import com.example.backend_spring.models.AjustementStock;
import com.example.backend_spring.models.Inventaire;
import com.example.backend_spring.models.LigneInventaire;
import com.example.backend_spring.models.LigneMouvementStock;
import com.example.backend_spring.models.Lot;
import com.example.backend_spring.models.MouvementStock;
import com.example.backend_spring.models.Stock;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.AjustementStockRepository;
import com.example.backend_spring.repositories.InventaireRepository;
import com.example.backend_spring.repositories.LigneInventaireRepository;
import com.example.backend_spring.repositories.LotRepository;
import com.example.backend_spring.repositories.StockRepository;
import com.example.backend_spring.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventaireWorkflowService {

    private final InventaireRepository inventaireRepository;
    private final LigneInventaireRepository ligneInventaireRepository;
    private final AjustementStockRepository ajustementStockRepository;
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockService mouvementStockService;

    @Transactional
    public List<LigneInventaire> genererLignes(int inventaireId) {
        Inventaire inventaire = inventaireRepository.findById(inventaireId)
                .orElseThrow(() -> new IllegalArgumentException("Inventaire introuvable"));

        if (inventaire.getDepot() == null) {
            throw new IllegalArgumentException("Depot obligatoire pour générer les lignes d'inventaire");
        }

        List<LigneInventaire> existantes = ligneInventaireRepository.findByInventaireId(inventaireId);
        if (!existantes.isEmpty()) {
            return existantes;
        }

        List<Stock> stocks = stockRepository.findByDepot(inventaire.getDepot());
        List<LigneInventaire> lignes = new ArrayList<>();

        for (Stock s : stocks) {
            LigneInventaire li = new LigneInventaire();
            li.setInventaire(inventaire);
            li.setArticle(s.getArticle());
            li.setEmplacement(s.getEmplacement());
            li.setQuantiteTheorique(s.getQuantite());
            li.setQuantitePhysique(null);
            li.setEcart(null);
            li.setMotifEcart(null);
            lignes.add(ligneInventaireRepository.save(li));
        }

        return lignes;
    }

    @Transactional
    public LigneInventaire saisirPhysique(int ligneId, Integer quantitePhysique, String motifEcart) {
        LigneInventaire ligne = ligneInventaireRepository.findById(ligneId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne inventaire introuvable"));

        if (quantitePhysique == null || quantitePhysique < 0) {
            throw new IllegalArgumentException("Quantité physique invalide");
        }

        Integer theo = ligne.getQuantiteTheorique() == null ? 0 : ligne.getQuantiteTheorique();
        int ecart = quantitePhysique - theo;

        ligne.setQuantitePhysique(quantitePhysique);
        ligne.setEcart(ecart);
        ligne.setMotifEcart(motifEcart);

        return ligneInventaireRepository.save(ligne);
    }

    @Transactional
    public Inventaire terminerInventaire(int inventaireId) {
        Inventaire inventaire = inventaireRepository.findById(inventaireId)
                .orElseThrow(() -> new IllegalArgumentException("Inventaire introuvable"));

        inventaire.setStatut("termine");
        inventaire.setDateFin(LocalDateTime.now());
        return inventaireRepository.save(inventaire);
    }

    @Transactional
    public List<AjustementStock> genererAjustements(int inventaireId, Integer utilisateurPropositionId, String motifParDefaut) {
        Inventaire inventaire = inventaireRepository.findById(inventaireId)
                .orElseThrow(() -> new IllegalArgumentException("Inventaire introuvable"));

        Utilisateur proposeur;
        if (utilisateurPropositionId != null) {
            proposeur = utilisateurRepository.findById(utilisateurPropositionId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur proposition introuvable"));
        } else if (inventaire.getUtilisateurLancement() != null) {
            proposeur = inventaire.getUtilisateurLancement();
        } else {
            throw new IllegalArgumentException("Utilisateur proposition obligatoire");
        }

        List<LigneInventaire> lignes = ligneInventaireRepository.findByInventaireId(inventaireId);
        if (lignes.isEmpty()) {
            throw new IllegalStateException("Aucune ligne inventaire. Générer les lignes d'abord.");
        }

        List<AjustementStock> existants = ajustementStockRepository.findByInventaireId(inventaireId);
        if (!existants.isEmpty()) {
            return existants;
        }

        List<AjustementStock> result = new ArrayList<>();
        String prefix = "AJ-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "-";
        int seq = 1;

        for (LigneInventaire li : lignes) {
            Integer ecart = li.getEcart();
            if (ecart == null || ecart == 0) {
                continue;
            }
            if (li.getEmplacement() == null) {
                throw new IllegalArgumentException("Emplacement obligatoire (Option A) sur la ligne inventaire");
            }

            AjustementStock a = new AjustementStock();
            a.setReference(prefix + String.format("%04d", seq++));
            a.setInventaire(inventaire);
            a.setArticle(li.getArticle());
            a.setEmplacement(li.getEmplacement());
            a.setQuantiteAjustee(Math.abs(ecart));
            a.setType(ecart > 0 ? "positif" : "negatif");
            String motif = li.getMotifEcart();
            if (motif == null || motif.isBlank()) {
                motif = motifParDefaut;
            }
            if (motif == null || motif.isBlank()) {
                motif = "Ajustement inventaire";
            }
            a.setMotif(motif);
            a.setStatut("propose");
            a.setUtilisateurProposition(proposeur);
            result.add(ajustementStockRepository.save(a));
        }

        return result;
    }

    @Transactional
    public AjustementStock validerAjustement(int ajustementId, int utilisateurValidationId, Integer lotId) {
        AjustementStock ajustement = ajustementStockRepository.findById(ajustementId)
                .orElseThrow(() -> new IllegalArgumentException("Ajustement introuvable"));

        if ("valide".equalsIgnoreCase(ajustement.getStatut())) {
            return ajustement;
        }

        Utilisateur valideur = utilisateurRepository.findById(utilisateurValidationId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur validation introuvable"));

        Inventaire inventaire = ajustement.getInventaire();
        if (inventaire == null) {
            throw new IllegalArgumentException("Inventaire obligatoire sur l'ajustement");
        }

        if (inventaire.getUtilisateurLancement() != null
                && inventaire.getUtilisateurLancement().getId() == valideur.getId()) {
            throw new IllegalStateException("La même personne ne peut pas lancer l'inventaire et valider les ajustements");
        }

        if (inventaire.getDepot() == null) {
            throw new IllegalArgumentException("Depot obligatoire sur l'inventaire");
        }
        if (ajustement.getEmplacement() == null) {
            throw new IllegalArgumentException("Emplacement obligatoire (Option A) sur l'ajustement");
        }
        if (ajustement.getArticle() == null) {
            throw new IllegalArgumentException("Article obligatoire sur l'ajustement");
        }

        if (ajustement.getArticle().isTraceableLot()) {
            if (lotId != null) {
                Lot lot = lotRepository.findById(lotId)
                        .orElseThrow(() -> new IllegalArgumentException("Lot introuvable"));
                ajustement.setLot(lot);
            }
            if (ajustement.getLot() == null) {
                throw new IllegalArgumentException("Lot obligatoire pour l'article traçable: " + ajustement.getArticle().getCode());
            }
        }

        int signedQty = "negatif".equalsIgnoreCase(ajustement.getType())
                ? -Math.abs(ajustement.getQuantiteAjustee())
                : Math.abs(ajustement.getQuantiteAjustee());

        MouvementStock mouvement = new MouvementStock();
        mouvement.setType("AJUSTEMENT");
        mouvement.setDepot(inventaire.getDepot());
        mouvement.setEmplacement(ajustement.getEmplacement());
        mouvement.setReferenceDocument(ajustement.getReference());
        mouvement.setMotif("Inventaire " + inventaire.getReference() + " - " + ajustement.getMotif());
        mouvement.setUtilisateur(valideur);

        List<LigneMouvementStock> lignes = new ArrayList<>();
        LigneMouvementStock l = new LigneMouvementStock();
        l.setArticle(ajustement.getArticle());
        l.setEmplacement(ajustement.getEmplacement());
        l.setQuantite(signedQty);
        l.setCoutUnitaire(null);
        l.setLot(ajustement.getLot());
        lignes.add(l);
        mouvement.setLignes(lignes);

        MouvementStock saved = mouvementStockService.saveMouvement(mouvement);
        mouvementStockService.validerMouvement(saved.getId());

        ajustement.setUtilisateurValidation(valideur);
        ajustement.setStatut("valide");
        return ajustementStockRepository.save(ajustement);
    }
}
