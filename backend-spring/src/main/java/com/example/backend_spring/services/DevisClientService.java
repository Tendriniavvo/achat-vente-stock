package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevisClientService {
    private final DevisClientRepository devisClientRepository;
    private final LigneDevisRepository ligneDevisRepository;
    private final ArticleRepository articleRepository;
    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JournalAuditService journalAuditService;
    private final CommandeClientRepository commandeClientRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final StockRepository stockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final LotRepository lotRepository;

    private static final BigDecimal PLAFOND_REMISE_COMMERCIAL = new BigDecimal("10.00");

    public List<DevisClient> getAllDevis() {
        return devisClientRepository.findAll();
    }

    public Optional<DevisClient> getDevisById(int id) {
        return devisClientRepository.findById(id);
    }

    public Optional<DevisClient> getDevisByReference(String reference) {
        return devisClientRepository.findByReference(reference);
    }

    @Transactional
    public DevisClient saveDevis(DevisClient devis) {
        return devisClientRepository.save(devis);
    }

    @Transactional
    public DevisClient updateStatut(int id, String statut, int utilisateurId) {
        DevisClient devis = devisClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Contrôle de validation pour les remises exceptionnelles ou conditions
        // particulières
        boolean needsValidation = devis.isRemiseExceptionnelle()
                || (devis.getNotes() != null && !devis.getNotes().trim().isEmpty());

        if (needsValidation && "envoye".equalsIgnoreCase(statut)
                && !"valide".equalsIgnoreCase(devis.getStatut())) {
            throw new RuntimeException(
                    "Ce devis contient des remises exceptionnelles ou des conditions particulières et doit être validé par un Responsable Ventes avant d'être envoyé.");
        }

        devis.setStatut(statut);
        DevisClient saved = devisClientRepository.save(devis);

        journalAuditService.logAction(
                utilisateur,
                "UPDATE_STATUT_DEVIS",
                "VENTES",
                saved.getReference(),
                "Statut modifié en : " + statut);

        return saved;
    }

    @Transactional
    public DevisClient validerDevis(int id, int responsableId) {
        DevisClient devis = devisClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));
        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isResponsable = responsable.getRoles().stream()
                .anyMatch(r -> r.getNom().equalsIgnoreCase("Responsable ventes")
                        || r.getNom().equalsIgnoreCase("Administrateur"));

        if (!isResponsable) {
            throw new RuntimeException("Seul un Responsable Ventes peut valider ce devis.");
        }

        devis.setStatut("valide");
        DevisClient saved = devisClientRepository.save(devis);

        journalAuditService.logAction(
                responsable,
                "VALIDATION_DEVIS",
                "VENTES",
                saved.getReference(),
                "Devis validé par le responsable " + responsable.getNom() + " (Remise exceptionnelle confirmée)");

        return saved;
    }

    @Transactional
    public CommandeClient transformerEnCommande(int devisId, int utilisateurId, String dateLivraisonStr) {
        DevisClient devis = devisClientRepository.findById(devisId)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!"valide".equalsIgnoreCase(devis.getStatut()) && !"envoye".equalsIgnoreCase(devis.getStatut())
                && !"brouillon".equalsIgnoreCase(devis.getStatut())) {
            // On accepte brouillon si pas de validation requise
            boolean needsValidation = devis.isRemiseExceptionnelle()
                    || (devis.getNotes() != null && !devis.getNotes().trim().isEmpty());
            if (needsValidation && !"valide".equalsIgnoreCase(devis.getStatut())) {
                throw new RuntimeException("Le devis doit être validé par un Responsable Ventes avant transformation");
            }
        }

        // Conversion de la date
        LocalDateTime dateLivraison = null;
        if (dateLivraisonStr != null && !dateLivraisonStr.isEmpty()) {
            try {
                // Support both ISO format and simple date format
                if (dateLivraisonStr.contains("T")) {
                    dateLivraison = LocalDateTime.parse(dateLivraisonStr);
                } else {
                    dateLivraison = LocalDateTime.parse(dateLivraisonStr + "T00:00:00");
                }
            } catch (Exception e) {
                throw new RuntimeException("Format de date de livraison invalide: " + dateLivraisonStr);
            }
        }

        // 1. Vérification de la disponibilité globale du stock
        List<LigneDevis> lignesDevis = ligneDevisRepository.findByDevisId(devisId);
        for (LigneDevis ld : lignesDevis) {
            checkStockAvailability(ld.getArticle(), ld.getQuantite());
        }

        // 2. Création de la commande client
        CommandeClient commande = new CommandeClient();
        commande.setDevis(devis);
        commande.setClient(devis.getClient());
        commande.setUtilisateur(utilisateur);
        commande.setReference("CC-" + System.currentTimeMillis());
        commande.setDateCommande(LocalDateTime.now());
        commande.setDateLivraisonPrevue(dateLivraison);
        commande.setMontantTotal(devis.getMontantTotal());
        commande.setStatut("reservee");

        CommandeClient savedCommande = commandeClientRepository.save(commande);

        // 3. Transformation des lignes et Réservation du stock
        for (LigneDevis ld : lignesDevis) {
            LigneCommandeClient lcc = new LigneCommandeClient();
            lcc.setCommande(savedCommande);
            lcc.setArticle(ld.getArticle());
            lcc.setQuantite(ld.getQuantite());
            lcc.setPrixUnitaire(ld.getPrixUnitaire());
            lcc.setRemise(ld.getRemise());
            ligneCommandeClientRepository.save(lcc);

            reserverStock(ld.getArticle(), ld.getQuantite(), savedCommande, utilisateur);
        }

        // 4. Mise à jour du statut du devis
        devis.setStatut("accepte");
        devisClientRepository.save(devis);

        journalAuditService.logAction(
                utilisateur,
                "TRANSFORMATION_DEVIS_COMMANDE",
                "VENTES",
                savedCommande.getReference(),
                "Commande créée et stock réservé à partir du devis " + devis.getReference());

        return savedCommande;
    }

    private void checkStockAvailability(Article article, int quantiteRequise) {
        List<Stock> stocks = stockRepository.findByArticleId(article.getId());
        int totalDisponible = stocks.stream().mapToInt(Stock::getQuantite).sum();

        if (totalDisponible < quantiteRequise) {
            throw new RuntimeException("Stock insuffisant pour l'article " + article.getNom()
                    + " (Disponible: " + totalDisponible + ", Requis: " + quantiteRequise + ")");
        }
    }

    private void reserverStock(Article article, int quantiteRequise, CommandeClient commande, Utilisateur utilisateur) {
        int restant = quantiteRequise;

        if (article.isTraceableLot()) {
            // Allocation FEFO (First Expired First Out) ou FIFO (First In First Out)
            List<Lot> lots;
            // Essayer FEFO si date expiration présente
            lots = lotRepository.findByArticleIdAndQuantiteGreaterThanAndConformeTrueOrderByDateExpirationAsc(
                    article.getId(), 0);

            // Si pas de lots avec expiration, utiliser FIFO (date d'entrée)
            if (lots.isEmpty() || lots.get(0).getDateExpiration() == null) {
                lots = lotRepository.findByArticleIdAndQuantiteGreaterThanAndConformeTrueOrderByDateEntreeAsc(
                        article.getId(), 0);
            }

            for (Lot lot : lots) {
                if (restant <= 0)
                    break;

                // Vérifier si le lot est expiré
                if (lot.getDateExpiration() != null && lot.getDateExpiration().isBefore(LocalDateTime.now())) {
                    continue;
                }

                int aPrendre = Math.min(lot.getQuantite(), restant);

                // Décrémenter le stock physique (réservation ferme)
                lot.setQuantite(lot.getQuantite() - aPrendre);
                lotRepository.save(lot);

                // Mettre à jour la table Stock globale et créer les mouvements de réservation
                updateGlobalStockAndCreateReservations(article, lot, aPrendre, commande, utilisateur);

                restant -= aPrendre;
            }
        } else {
            // Article non géré par lot -> FIFO simple sur les entrées de stock ou global
            updateGlobalStockAndCreateReservations(article, null, restant, commande, utilisateur);
            restant = 0;
        }

        if (restant > 0) {
            throw new RuntimeException("Impossible de réserver la totalité du stock pour " + article.getNom()
                    + ". Manquant: " + restant + ". Vérifiez la conformité et les dates d'expiration des lots.");
        }
    }

    private void updateGlobalStockAndCreateReservations(Article article, Lot lot, int quantiteARetirer,
            CommandeClient commande, Utilisateur utilisateur) {
        List<Stock> stocks = stockRepository.findByArticleId(article.getId());
        int aRetirer = quantiteARetirer;

        for (Stock s : stocks) {
            if (aRetirer <= 0)
                break;
            int dispo = s.getQuantite();
            if (dispo <= 0)
                continue;

            int pris = Math.min(dispo, aRetirer);
            s.setQuantite(dispo - pris);
            s.setDateMaj(LocalDateTime.now());
            stockRepository.save(s);

            // Créer le mouvement de réservation avec les infos de dépôt/emplacement
            createReservationMovement(article, lot, pris, commande, utilisateur, s.getDepot(), s.getEmplacement());

            aRetirer -= pris;
        }

        if (aRetirer > 0) {
            throw new RuntimeException("Stock insuffisant dans les dépôts pour " + article.getNom());
        }
    }

    private void createReservationMovement(Article article, Lot lot, int quantite, CommandeClient commande,
            Utilisateur utilisateur, Depot depot, Emplacement emplacement) {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setType("reservation");
        mouvement.setArticle(article);
        mouvement.setLot(lot);
        mouvement.setQuantite(quantite);
        mouvement.setDepot(depot);
        mouvement.setEmplacement(emplacement);
        mouvement.setReferenceDocument(commande.getReference());
        mouvement.setUtilisateur(utilisateur);
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvement.setMotif("Réservation pour commande client " + commande.getReference());
        mouvementStockRepository.save(mouvement);
    }

    @Transactional
    public DevisClient creerDevis(int clientId, int utilisateurId, String notes, List<LigneDevis> lignes) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        DevisClient devis = new DevisClient();
        devis.setClient(client);
        devis.setUtilisateur(utilisateur);
        devis.setReference("DEV-" + System.currentTimeMillis());
        devis.setStatut("brouillon");
        devis.setNotes(notes);
        devis.setDateDevis(LocalDateTime.now());
        devis.setDateValidite(LocalDateTime.now().plusDays(30)); // Validité par défaut 30 jours

        BigDecimal montantTotal = BigDecimal.ZERO;
        boolean hasRemiseExceptionnelle = false;

        // Première passe pour calculer le montant total et vérifier les remises
        for (LigneDevis ligne : lignes) {
            Article article = articleRepository.findById(ligne.getArticle().getId())
                    .orElseThrow(() -> new RuntimeException("Article non trouvé: " + ligne.getArticle().getId()));

            BigDecimal prixUnitaire = ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire()
                    : article.getPrixVente();
            BigDecimal remise = ligne.getRemise() != null ? ligne.getRemise() : BigDecimal.ZERO;

            // Vérification du plafond de remise pour le rôle Commercial
            boolean isCommercial = utilisateur.getRoles().stream()
                    .anyMatch(r -> r.getNom().equalsIgnoreCase("Commercial"));

            if (isCommercial && remise.compareTo(PLAFOND_REMISE_COMMERCIAL) > 0) {
                hasRemiseExceptionnelle = true;
            }

            BigDecimal totalLigne = prixUnitaire.multiply(new BigDecimal(ligne.getQuantite()));
            BigDecimal montantRemise = totalLigne.multiply(remise).divide(new BigDecimal("100"));
            montantTotal = montantTotal.add(totalLigne.subtract(montantRemise));
        }

        devis.setMontantTotal(montantTotal);
        devis.setRemiseExceptionnelle(hasRemiseExceptionnelle);

        DevisClient savedDevis = devisClientRepository.save(devis);

        for (LigneDevis ligne : lignes) {
            ligne.setDevis(savedDevis);
            if (ligne.getPrixUnitaire() == null) {
                Article article = articleRepository.findById(ligne.getArticle().getId()).get();
                ligne.setPrixUnitaire(article.getPrixVente());
            }
            ligneDevisRepository.save(ligne);
        }

        journalAuditService.logAction(
                utilisateur,
                "CREATION_DEVIS",
                "VENTES",
                savedDevis.getReference(),
                "Création d'un nouveau devis pour le client " + client.getNom() + " (Montant: " + montantTotal + ")");

        return savedDevis;
    }

    @Transactional
    public void deleteDevis(int id) {
        List<LigneDevis> lignes = ligneDevisRepository.findByDevisId(id);
        ligneDevisRepository.deleteAll(lignes);
        devisClientRepository.deleteById(id);
    }
}
