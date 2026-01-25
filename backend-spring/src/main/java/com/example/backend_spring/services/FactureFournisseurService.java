package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureFournisseurService {

    private final FactureFournisseurRepository factureFournisseurRepository;
    private final LigneFactureFournisseurRepository ligneFactureFournisseurRepository;
    private final LigneReceptionRepository ligneReceptionRepository;
    private final LigneBonCommandeRepository ligneBonCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ReceptionRepository receptionRepository;
    private final JournalAuditService journalAuditService;
    private final BudgetService budgetService;

    public List<FactureFournisseur> getAllFactures() {
        return factureFournisseurRepository.findAll();
    }

    public Optional<FactureFournisseur> getFactureById(int id) {
        return factureFournisseurRepository.findById(id);
    }

    public Optional<FactureFournisseur> getFactureByReference(String reference) {
        return factureFournisseurRepository.findByReference(reference);
    }

    public List<LigneFactureFournisseur> getLignesByFactureId(int factureId) {
        return ligneFactureFournisseurRepository.findByFactureId(factureId);
    }

    @Transactional
    public FactureFournisseur genererFactureDepuisReception(int receptionId) {
        Reception reception = receptionRepository.findById(receptionId)
                .orElseThrow(() -> new RuntimeException("Réception non trouvée"));

        if (factureFournisseurRepository.findByReceptionId(receptionId).isPresent()) {
            throw new RuntimeException("Une facture existe déjà pour cette réception");
        }

        FactureFournisseur facture = new FactureFournisseur();
        facture.setReference("FAC-" + System.currentTimeMillis());
        facture.setBonCommande(reception.getBonCommande());
        facture.setReception(reception);
        facture.setFournisseur(reception.getBonCommande().getFournisseur());
        facture.setDateFacture(LocalDateTime.now());
        facture.setStatut("attente");

        BigDecimal montantTotal = BigDecimal.ZERO;
        List<LigneReception> lignesRec = ligneReceptionRepository.findByReceptionId(receptionId);
        List<LigneBonCommande> lignesBC = ligneBonCommandeRepository
                .findByBonCommandeId(reception.getBonCommande().getId());
        List<LigneFactureFournisseur> lignesFacture = new ArrayList<>();

        for (LigneReception lr : lignesRec) {
            LigneFactureFournisseur lf = new LigneFactureFournisseur();
            lf.setFacture(facture);
            lf.setArticle(lr.getArticle());
            lf.setQuantite(lr.getQuantiteRecue());

            // Chercher le prix dans le BC
            BigDecimal prixUnitaire = lignesBC.stream()
                    .filter(l -> l.getArticle().getId() == lr.getArticle().getId())
                    .map(LigneBonCommande::getPrixUnitaire)
                    .findFirst()
                    .orElse(lr.getArticle().getPrixAchat());

            lf.setPrixUnitaire(prixUnitaire);
            lf.setTaxe(lr.getArticle().getTaxe());

            BigDecimal ligneTotal = prixUnitaire.multiply(new BigDecimal(lr.getQuantiteRecue()));
            if (lf.getTaxe() != null) {
                ligneTotal = ligneTotal.add(ligneTotal.multiply(lf.getTaxe().getTaux().divide(new BigDecimal(100))));
            }
            montantTotal = montantTotal.add(ligneTotal);
            lignesFacture.add(lf);
        }

        facture.setMontantTotal(montantTotal);
        FactureFournisseur savedFacture = factureFournisseurRepository.save(facture);
        ligneFactureFournisseurRepository.saveAll(lignesFacture);

        return savedFacture;
    }

    @Transactional
    public FactureFournisseur saveFacture(FactureFournisseur facture) {
        return factureFournisseurRepository.save(facture);
    }

    @Transactional
    public void deleteFacture(int id) {
        factureFournisseurRepository.deleteById(id);
    }

    @Transactional
    public FactureFournisseur rapprocherFacture(int factureId, int utilisateurId) {
        FactureFournisseur facture = factureFournisseurRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 1. Contrôle de rôle : Finance
        boolean isFinance = utilisateur.getRoles().stream()
                .anyMatch(role -> role.getNom().equalsIgnoreCase("Finance")
                        || role.getNom().equalsIgnoreCase("Administrateur"));
        if (!isFinance) {
            throw new RuntimeException("Seul le rôle Finance peut rapprocher une facture");
        }

        // 2. Séparation des tâches : Le réceptionneur ne peut pas valider la facture
        if (facture.getReception() != null && facture.getReception().getUtilisateur() != null) {
            if (facture.getReception().getUtilisateur().getId() == utilisateurId) {
                throw new RuntimeException(
                        "Séparation des tâches : l'utilisateur ayant effectué la réception ne peut pas valider la facture");
            }
        }

        List<LigneFactureFournisseur> lignesFacture = ligneFactureFournisseurRepository.findByFactureId(factureId);
        List<LigneReception> lignesReception = facture.getReception() != null
                ? ligneReceptionRepository.findByReceptionId(facture.getReception().getId())
                : new ArrayList<>();
        List<LigneBonCommande> lignesBC = facture.getBonCommande() != null
                ? ligneBonCommandeRepository.findByBonCommandeId(facture.getBonCommande().getId())
                : new ArrayList<>();

        List<String> erreurs = new ArrayList<>();
        BigDecimal totalCalcule = BigDecimal.ZERO;

        for (LigneFactureFournisseur lf : lignesFacture) {
            Article article = lf.getArticle();

            // 3-way match: Facture vs BC (Prix et Quantité)
            Optional<LigneBonCommande> lbcOpt = lignesBC.stream()
                    .filter(l -> l.getArticle().getId() == article.getId())
                    .findFirst();

            if (lbcOpt.isPresent()) {
                LigneBonCommande lbc = lbcOpt.get();
                if (lf.getPrixUnitaire().compareTo(lbc.getPrixUnitaire()) != 0) {
                    erreurs.add("Article " + article.getCode() + " : Prix facture (" + lf.getPrixUnitaire()
                            + ") diffère du prix BC (" + lbc.getPrixUnitaire() + ")");
                }
                if (lf.getQuantite() > lbc.getQuantite()) {
                    erreurs.add("Article " + article.getCode() + " : Quantité facturée (" + lf.getQuantite()
                            + ") supérieure à la quantité commandée (" + lbc.getQuantite() + ")");
                }
            } else {
                erreurs.add("Article " + article.getCode() + " facturé mais non présent dans le bon de commande");
            }

            // 3-way match: Facture vs Réception (Quantité)
            if (facture.getReception() != null) {
                Optional<LigneReception> lrOpt = lignesReception.stream()
                        .filter(l -> l.getArticle().getId() == article.getId())
                        .findFirst();

                if (lrOpt.isPresent()) {
                    LigneReception lr = lrOpt.get();
                    if (lf.getQuantite() > lr.getQuantiteRecue()) {
                        erreurs.add("Article " + article.getCode() + " : Quantité facturée (" + lf.getQuantite()
                                + ") supérieure à la quantité reçue (" + lr.getQuantiteRecue() + ")");
                    }
                } else {
                    erreurs.add("Article " + article.getCode() + " facturé mais non présent dans le bon de réception");
                }
            }

            // Calcul du total pour vérification TVA/Montants
            BigDecimal montantLigne = lf.getPrixUnitaire().multiply(new BigDecimal(lf.getQuantite()));
            if (lf.getTaxe() != null) {
                BigDecimal taxe = lf.getTaxe().getTaux().divide(new BigDecimal(100));
                montantLigne = montantLigne.add(montantLigne.multiply(taxe));
            }
            totalCalcule = totalCalcule.add(montantLigne);
        }

        // Vérification du montant total
        if (facture.getMontantTotal() != null && totalCalcule.compareTo(facture.getMontantTotal()) != 0) {
            erreurs.add("Montant total de la facture (" + facture.getMontantTotal()
                    + ") ne correspond pas au calcul des lignes (" + totalCalcule + ")");
        }

        if (!erreurs.isEmpty()) {
            facture.setStatut("bloquee");
            facture.setEcarts(String.join(" | ", erreurs));
            log.warn("Rapprochement facture {} bloqué : {}", facture.getReference(), erreurs);
        } else {
            facture.setStatut("validee");
            facture.setEcarts(null);
            facture.setUtilisateurValidation(utilisateur);
            log.info("Rapprochement facture {} réussi par {}", facture.getReference(), utilisateur.getNom());
        }

        FactureFournisseur saved = factureFournisseurRepository.save(facture);

        // Journal Audit
        journalAuditService.logAction(
                utilisateur,
                erreurs.isEmpty() ? "VALIDATION_FACTURE" : "BLOCAGE_FACTURE",
                "FINANCE",
                facture.getReference(),
                erreurs.isEmpty() ? "Facture rapprochée avec succès"
                        : "Écarts détectés : " + String.join(", ", erreurs));

        return saved;
    }

    @Transactional
    public FactureFournisseur payerFacture(int factureId, int utilisateurId) {
        FactureFournisseur facture = factureFournisseurRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 1. Contrôle de rôle : Finance ou DAF
        boolean isAuthorized = utilisateur.getRoles().stream()
                .anyMatch(role -> role.getNom().equalsIgnoreCase("Finance")
                        || role.getNom().equalsIgnoreCase("DAF")
                        || role.getNom().equalsIgnoreCase("Administrateur"));
        if (!isAuthorized) {
            throw new RuntimeException("Seul le rôle Finance ou DAF peut effectuer un paiement");
        }

        // 2. Vérification du statut : La facture doit être validée par 3-way match
        if (!"validee".equalsIgnoreCase(facture.getStatut())) {
            throw new RuntimeException("La facture doit être validée (3-way match) avant d'être payée. Statut actuel: "
                    + facture.getStatut());
        }

        // 3. Contrôles de seuils (Exemple: Seuil de 10.000.000 MGA pour validation
        // exceptionnelle)
        BigDecimal seuilExceptionnel = new BigDecimal("10000000");
        boolean isExceptional = facture.getMontantTotal().compareTo(seuilExceptionnel) > 0;

        // 4. Mise à jour du statut
        facture.setStatut("payee");

        FactureFournisseur saved = factureFournisseurRepository.save(facture);

        // 5. Mise à jour du budget du département
        try {
            if (facture.getBonCommande() != null &&
                    facture.getBonCommande().getDemandeAchat() != null &&
                    facture.getBonCommande().getDemandeAchat().getDemandeur() != null &&
                    facture.getBonCommande().getDemandeAchat().getDemandeur().getDepartement() != null) {

                Departement dept = facture.getBonCommande().getDemandeAchat().getDemandeur().getDepartement();
                budgetService.consommerBudget(dept, facture.getMontantTotal());
                log.info("Budget consommé pour le département {}: {}", dept.getNom(), facture.getMontantTotal());
            } else {
                log.warn("Impossible de mettre à jour le budget : Département non trouvé pour la facture {}",
                        facture.getReference());
            }
        } catch (Exception e) {
            log.error("Erreur lors de la consommation du budget pour la facture {}: {}",
                    facture.getReference(), e.getMessage());
            // On peut choisir de ne pas bloquer le paiement si le budget échoue,
            // ou de relancer l'exception selon la politique métier.
            // Ici, on relance pour garantir la cohérence financière.
            throw new RuntimeException("Erreur budget : " + e.getMessage());
        }

        // 6. Traçabilité complète pour audits & Réduction des risques de fraude
        String detailAction = "Paiement effectué pour la facture " + facture.getReference();
        if (isExceptional) {
            detailAction += " (Validation exceptionnelle : Montant > " + seuilExceptionnel + ")";
            log.info("Paiement exceptionnel détecté pour la facture {}: {} par {}",
                    facture.getReference(), facture.getMontantTotal(), utilisateur.getNom());
        }

        journalAuditService.logAction(
                utilisateur,
                "PAIEMENT_FACTURE",
                isAuthorized ? "FINANCE_DAF" : "FINANCE",
                facture.getReference(),
                detailAction);

        return saved;
    }
}
