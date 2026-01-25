package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureClientService {

    private final FactureClientRepository factureClientRepository;
    private final LigneFactureClientRepository ligneFactureClientRepository;
    private final LivraisonRepository livraisonRepository;
    private final LigneLivraisonRepository ligneLivraisonRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;
    private final JournalAuditRepository journalAuditRepository;
    private final CommandeClientRepository commandeClientRepository;

    public List<FactureClient> getAllFactures() {
        return factureClientRepository.findAll();
    }

    public Optional<FactureClient> getFactureById(int id) {
        return factureClientRepository.findById(id);
    }

    public Optional<FactureClient> getFactureByReference(String reference) {
        return factureClientRepository.findByReference(reference);
    }

    public Optional<FactureClient> getFactureByLivraisonId(int livraisonId) {
        return factureClientRepository.findByLivraisonId(livraisonId);
    }

    public List<LigneFactureClient> getLignesByFactureId(int factureId) {
        return ligneFactureClientRepository.findByFactureId(factureId);
    }

    @Transactional
    public FactureClient saveFacture(FactureClient facture) {
        return factureClientRepository.save(facture);
    }

    @Transactional
    public FactureClient genererFactureDepuisLivraison(int livraisonId, int utilisateurId) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));

        if (!"expediee".equalsIgnoreCase(livraison.getStatut())) {
            throw new RuntimeException("La livraison doit être expédiée avant de générer la facture.");
        }

        // Vérifier si une facture existe déjà pour cette livraison
        if (factureClientRepository.existsByLivraisonId(livraison.getId())) {
            throw new RuntimeException("Une facture a déjà été générée pour cette livraison.");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CommandeClient commande = livraison.getCommandeClient();
        List<LigneLivraison> lignesLivraison = ligneLivraisonRepository.findByLivraisonId(livraisonId);
        List<LigneCommandeClient> lignesCommande = ligneCommandeClientRepository.findByCommandeId(commande.getId());

        FactureClient facture = new FactureClient();
        facture.setReference("FACT-" + System.currentTimeMillis());
        facture.setClient(commande.getClient());
        facture.setCommandeClient(commande);
        facture.setLivraison(livraison);
        facture.setDateFacture(LocalDateTime.now());
        facture.setStatut("attente");

        FactureClient savedFacture = factureClientRepository.save(facture);

        BigDecimal montantTotalTotal = BigDecimal.ZERO;

        for (LigneLivraison ll : lignesLivraison) {
            // Contrôle de cohérence : trouver l'article correspondant dans la commande
            LigneCommandeClient lcc = lignesCommande.stream()
                    .filter(l -> l.getArticle().getId() == ll.getArticle().getId())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Incohérence : L'article " + ll.getArticle().getNom()
                            + " est présent dans la livraison mais pas dans la commande d'origine."));

            // Contrôle de prix : le prix unitaire doit être cohérent avec la commande
            // (Ici on utilise celui de la commande par défaut, mais on pourrait vérifier
            // s'il a changé)
            BigDecimal prixUnitaire = lcc.getPrixUnitaire();

            LigneFactureClient lfc = new LigneFactureClient();
            lfc.setFacture(savedFacture);
            lfc.setArticle(ll.getArticle());
            lfc.setQuantite(ll.getQuantiteLivree());
            lfc.setPrixUnitaire(prixUnitaire);
            lfc.setTaxe(ll.getArticle().getTaxe());

            ligneFactureClientRepository.save(lfc);

            // Calcul du montant HT (Prix * Quantité livrée)
            BigDecimal montantHT = prixUnitaire.multiply(new BigDecimal(ll.getQuantiteLivree()));

            // Appliquer la remise de la commande (si présente)
            BigDecimal remiseTaux = lcc.getRemise() != null ? lcc.getRemise() : BigDecimal.ZERO;
            BigDecimal montantRemise = montantHT.multiply(remiseTaux).divide(new BigDecimal("100"), 2,
                    BigDecimal.ROUND_HALF_UP);
            BigDecimal montantApresRemise = montantHT.subtract(montantRemise);

            // Appliquer la taxe (TVA) sur le montant après remise
            BigDecimal tauxTaxe = ll.getArticle().getTaxe() != null ? ll.getArticle().getTaxe().getTaux()
                    : BigDecimal.ZERO;
            BigDecimal montantTaxe = montantApresRemise.multiply(tauxTaxe).divide(new BigDecimal("100"), 2,
                    BigDecimal.ROUND_HALF_UP);

            montantTotalTotal = montantTotalTotal.add(montantApresRemise).add(montantTaxe);
        }

        savedFacture.setMontantTotal(montantTotalTotal);
        FactureClient finalFacture = factureClientRepository.save(savedFacture);

        // Mise à jour du statut de la commande
        commande.setStatut("facturee");
        commandeClientRepository.save(commande);

        journalAuditService.logAction(
                utilisateur,
                "GENERATION_FACTURE",
                "VENTES",
                finalFacture.getReference(),
                "Facture générée pour la livraison " + livraison.getReference() + ". Montant: " + montantTotalTotal);

        return finalFacture;
    }

    @Transactional
    public FactureClient creerAvoir(int factureOriginaleId, String motif, int utilisateurId,
            List<LigneAvoirRequest> lignesAvoir) {
        FactureClient factureOrig = factureClientRepository.findById(factureOriginaleId)
                .orElseThrow(() -> new RuntimeException("Facture d'origine non trouvée"));

        if (!"validee".equalsIgnoreCase(factureOrig.getStatut())
                && !"payee".equalsIgnoreCase(factureOrig.getStatut())) {
            throw new RuntimeException("Seule une facture validée ou payée peut faire l'objet d'un avoir.");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        FactureClient avoir = new FactureClient();
        avoir.setReference("AVOIR-" + System.currentTimeMillis());
        avoir.setClient(factureOrig.getClient());
        avoir.setCommandeClient(factureOrig.getCommandeClient());
        avoir.setLivraison(factureOrig.getLivraison());
        avoir.setDateFacture(LocalDateTime.now());
        avoir.setStatut("attente");

        FactureClient savedAvoir = factureClientRepository.save(avoir);
        BigDecimal montantTotalAvoir = BigDecimal.ZERO;

        if (lignesAvoir == null || lignesAvoir.isEmpty()) {
            // Avoir total par défaut si aucune ligne spécifiée
            List<LigneFactureClient> lignesOrig = ligneFactureClientRepository.findByFactureId(factureOriginaleId);
            for (LigneFactureClient lfOrig : lignesOrig) {
                LigneFactureClient lfAvoir = new LigneFactureClient();
                lfAvoir.setFacture(savedAvoir);
                lfAvoir.setArticle(lfOrig.getArticle());
                lfAvoir.setQuantite(lfOrig.getQuantite());
                lfAvoir.setPrixUnitaire(lfOrig.getPrixUnitaire().negate());
                lfAvoir.setTaxe(lfOrig.getTaxe());
                ligneFactureClientRepository.save(lfAvoir);

                BigDecimal montantHT = lfOrig.getPrixUnitaire().multiply(new BigDecimal(lfOrig.getQuantite()));
                BigDecimal tauxTaxe = lfOrig.getTaxe() != null ? lfOrig.getTaxe().getTaux() : BigDecimal.ZERO;
                BigDecimal montantTaxe = montantHT.multiply(tauxTaxe).divide(new BigDecimal("100"), 2,
                        BigDecimal.ROUND_HALF_UP);
                montantTotalAvoir = montantTotalAvoir.add(montantHT).add(montantTaxe);
            }
        } else {
            // Avoir partiel
            for (LigneAvoirRequest req : lignesAvoir) {
                LigneFactureClient lfOrig = ligneFactureClientRepository.findById(req.getLigneFactureId())
                        .orElseThrow(
                                () -> new RuntimeException("Ligne de facture non trouvée: " + req.getLigneFactureId()));

                if (req.getQuantite() > lfOrig.getQuantite()) {
                    throw new RuntimeException("La quantité de l'avoir ne peut pas dépasser la quantité facturée.");
                }

                LigneFactureClient lfAvoir = new LigneFactureClient();
                lfAvoir.setFacture(savedAvoir);
                lfAvoir.setArticle(lfOrig.getArticle());
                lfAvoir.setQuantite(req.getQuantite());
                lfAvoir.setPrixUnitaire(lfOrig.getPrixUnitaire().negate());
                lfAvoir.setTaxe(lfOrig.getTaxe());
                ligneFactureClientRepository.save(lfAvoir);

                BigDecimal montantHT = lfOrig.getPrixUnitaire().multiply(new BigDecimal(req.getQuantite()));
                BigDecimal tauxTaxe = lfOrig.getTaxe() != null ? lfOrig.getTaxe().getTaux() : BigDecimal.ZERO;
                BigDecimal montantTaxe = montantHT.multiply(tauxTaxe).divide(new BigDecimal("100"), 2,
                        BigDecimal.ROUND_HALF_UP);
                montantTotalAvoir = montantTotalAvoir.add(montantHT).add(montantTaxe);
            }
        }

        savedAvoir.setMontantTotal(montantTotalAvoir.negate());
        factureClientRepository.save(savedAvoir);

        journalAuditService.logAction(
                utilisateur,
                "CREATION_AVOIR",
                "VENTES",
                savedAvoir.getReference(),
                "Avoir créé pour la facture " + factureOrig.getReference() + ". Motif: " + motif + ". Montant: "
                        + savedAvoir.getMontantTotal());

        return savedAvoir;
    }

    // DTO interne pour la requête d'avoir partiel
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LigneAvoirRequest {
        private int ligneFactureId;
        private int quantite;
    }

    @Transactional
    public FactureClient validerFacture(int id, int utilisateurId) {
        FactureClient facture = factureClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Contrôle de rôle : Finance ou Administrateur
        boolean isAuthorized = utilisateur.getRoles().stream()
                .anyMatch(role -> role.getNom().equalsIgnoreCase("Finance")
                        || role.getNom().equalsIgnoreCase("Administrateur"));
        if (!isAuthorized) {
            throw new RuntimeException("Seul le rôle Finance peut valider une facture.");
        }

        // Séparation des tâches :
        // 1. Celui qui a créé le client ne doit pas valider la facture ou l'avoir
        List<JournalAudit> creationAudits = journalAuditRepository.findByModuleAndReferenceObjetOrderByDateActionDesc(
                "CLIENT", String.valueOf(facture.getClient().getId()));

        Optional<JournalAudit> creationAudit = creationAudits.stream()
                .filter(a -> "CRÉATION".equalsIgnoreCase(a.getAction()))
                .findFirst();

        if (creationAudit.isPresent() && creationAudit.get().getUtilisateur().getId() == utilisateurId) {
            throw new RuntimeException(
                    "Séparation des tâches : Le créateur du client ne peut pas valider ses factures.");
        }

        // 2. Si c'est un avoir, on peut ajouter une vérification supplémentaire
        if (facture.getReference().startsWith("AVOIR-")) {
            // Vérifier que le validateur n'est pas celui qui a créé l'avoir
            List<JournalAudit> avoirAudits = journalAuditRepository.findByModuleAndReferenceObjetOrderByDateActionDesc(
                    "VENTES", facture.getReference());

            Optional<JournalAudit> creationAvoirAudit = avoirAudits.stream()
                    .filter(a -> "CREATION_AVOIR".equalsIgnoreCase(a.getAction()))
                    .findFirst();

            if (creationAvoirAudit.isPresent() && creationAvoirAudit.get().getUtilisateur().getId() == utilisateurId) {
                throw new RuntimeException(
                        "Séparation des tâches : Le créateur de l'avoir ne peut pas le valider lui-même.");
            }
        }

        facture.setStatut("validee");
        facture.setUtilisateurValidation(utilisateur);
        FactureClient saved = factureClientRepository.save(facture);

        String actionLabel = facture.getReference().startsWith("AVOIR-") ? "VALIDATION_AVOIR" : "VALIDATION_FACTURE";

        journalAuditService.logAction(
                utilisateur,
                actionLabel,
                "VENTES",
                saved.getReference(),
                (facture.getReference().startsWith("AVOIR-") ? "Avoir" : "Facture") + " validé par "
                        + utilisateur.getNom());

        return saved;
    }

    @Transactional
    public void deleteFacture(int id) {
        List<LigneFactureClient> lignes = ligneFactureClientRepository.findByFactureId(id);
        ligneFactureClientRepository.deleteAll(lignes);
        factureClientRepository.deleteById(id);
    }
}
