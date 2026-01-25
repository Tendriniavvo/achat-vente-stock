package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonCommandeFournisseurService {

    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
    private final DemandeAchatRepository demandeAchatRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final LigneBonCommandeRepository ligneBonCommandeRepository;
    private final JournalAuditService journalAuditService;

    private static final BigDecimal SEUIL_RESPONSABLE = new BigDecimal("10000000"); // 10 millions MGA

    public List<BonCommandeFournisseur> getAllBonCommandes() {
        List<BonCommandeFournisseur> list = bonCommandeFournisseurRepository.findAll();
        log.info("Liste des BC en base :");
        for (BonCommandeFournisseur bc : list) {
            log.info("ID: {}, Ref: {}, Statut: {}", bc.getId(), bc.getReference(), bc.getStatut());
        }
        return list;
    }

    public Optional<BonCommandeFournisseur> getBonCommandeById(int id) {
        return bonCommandeFournisseurRepository.findById(id);
    }

    public Optional<BonCommandeFournisseur> getBonCommandeByReference(String reference) {
        return bonCommandeFournisseurRepository.findByReference(reference);
    }

    @Transactional
    public BonCommandeFournisseur saveBonCommande(BonCommandeFournisseur bonCommande) {
        return bonCommandeFournisseurRepository.save(bonCommande);
    }

    @Transactional
    public void deleteBonCommande(int id) {
        bonCommandeFournisseurRepository.deleteById(id);
    }

    @Transactional
    public BonCommandeFournisseur transformerEnBonCommande(int demandeAchatId, int acheteurId, Integer fournisseurId,
            String dateLivraisonPrevue) {
        // Utiliser findByIdWithDetails pour charger explicitement les lignes de la DA
        DemandeAchat demande = demandeAchatRepository.findByIdWithDetails(demandeAchatId)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!"approuvé".equals(demande.getStatut()) && !"approuve".equals(demande.getStatut())) {
            throw new RuntimeException(
                    "Seule une demande approuvée peut être transformée en Bon de Commande. Statut actuel: "
                            + demande.getStatut());
        }

        Utilisateur acheteur = utilisateurRepository.findById(acheteurId)
                .orElseThrow(() -> new RuntimeException("Acheteur non trouvé"));

        // Contrôle : L'acheteur ne peut pas être le créateur de la DA
        if (demande.getDemandeur() != null && demande.getDemandeur().getId() == acheteurId) {
            throw new RuntimeException("Séparation des tâches : L'acheteur ne peut pas être le demandeur de la DA");
        }

        BonCommandeFournisseur bc = new BonCommandeFournisseur();
        bc.setDemandeAchat(demande);
        bc.setUtilisateur(acheteur);
        bc.setDateCommande(LocalDateTime.now());

        if (dateLivraisonPrevue != null && !dateLivraisonPrevue.isEmpty()) {
            try {
                if (dateLivraisonPrevue.contains("T")) {
                    bc.setDateLivraisonPrevue(LocalDateTime.parse(dateLivraisonPrevue));
                } else {
                    bc.setDateLivraisonPrevue(java.time.LocalDate.parse(dateLivraisonPrevue).atStartOfDay());
                }
            } catch (Exception e) {
                log.warn("Format de date invalide: {}, erreur: {}", dateLivraisonPrevue, e.getMessage());
            }
        }

        bc.setStatut("brouillon");
        bc.setReference(genererReference());

        if (fournisseurId != null) {
            Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
            bc.setFournisseur(fournisseur);
        }

        BigDecimal montantTotal = BigDecimal.ZERO;

        // Sauvegarder d'abord le BC pour avoir son ID
        BonCommandeFournisseur savedBc = bonCommandeFournisseurRepository.save(bc);

        // Transformer les lignes
        if (demande.getLignes() != null) {
            for (LigneDemandeAchat ligneDa : demande.getLignes()) {
                LigneBonCommande ligneBc = new LigneBonCommande();
                ligneBc.setBonCommande(savedBc);
                ligneBc.setArticle(ligneDa.getArticle());
                ligneBc.setQuantite(ligneDa.getQuantite());
                ligneBc.setPrixUnitaire(ligneDa.getPrixEstime()); // Par défaut le prix estimé
                ligneBc.setRemise(BigDecimal.ZERO);

                ligneBonCommandeRepository.save(ligneBc);

                BigDecimal ligneTotal = ligneDa.getPrixEstime().multiply(new BigDecimal(ligneDa.getQuantite()));
                montantTotal = montantTotal.add(ligneTotal);
            }
        }

        savedBc.setMontantTotal(montantTotal);

        // Mettre à jour le statut de la DA
        demande.setStatut("transformé");
        String historique = demande.getHistoriqueValidations();
        if (historique == null)
            historique = "";
        historique += LocalDateTime.now() + ": Transformation en Bon de Commande " + savedBc.getReference() + " par "
                + acheteur.getNom() + "\n";
        demande.setHistoriqueValidations(historique);
        demandeAchatRepository.save(demande);

        return bonCommandeFournisseurRepository.save(savedBc);
    }

    private String genererReference() {
        // Utiliser l'ID maximum + 1 au lieu de count() pour éviter les doublons si
        // suppression
        Integer maxId = bonCommandeFournisseurRepository.findAll().stream()
                .map(BonCommandeFournisseur::getId)
                .max(Integer::compare)
                .orElse(0);
        int nextNum = maxId + 1;
        int annee = LocalDateTime.now().getYear();
        return String.format("BC-%d-%04d", annee, nextNum);
    }

    public List<LigneBonCommande> getLignesByBcId(int bcId) {
        log.info("Récupération des lignes pour le BC ID: {}", bcId);
        List<LigneBonCommande> lignes = ligneBonCommandeRepository.findByBonCommandeId(bcId);
        log.info("Nombre de lignes trouvées en base pour BC ID {}: {}", bcId, lignes.size());

        // Si aucune ligne n'est trouvée, on vérifie si on peut les récupérer de la DA
        // (Utile pour corriger les BC créés lors d'un bug de transformation)
        if (lignes.isEmpty()) {
            log.info("Aucune ligne trouvée pour BC ID {}. Tentative d'auto-réparation...", bcId);
            Optional<BonCommandeFournisseur> bcOpt = bonCommandeFournisseurRepository.findById(bcId);
            if (bcOpt.isPresent()) {
                BonCommandeFournisseur bc = bcOpt.get();
                if (bc.getDemandeAchat() != null) {
                    log.info("DA source trouvée: ID {}. Récupération des détails...", bc.getDemandeAchat().getId());
                    // Charger la DA avec ses lignes
                    Optional<DemandeAchat> daOpt = demandeAchatRepository
                            .findByIdWithDetails(bc.getDemandeAchat().getId());
                    if (daOpt.isPresent()) {
                        DemandeAchat da = daOpt.get();
                        log.info("DA trouvée en base avec {} lignes.",
                                da.getLignes() != null ? da.getLignes().size() : 0);
                        if (da.getLignes() != null && !da.getLignes().isEmpty()) {
                            // Créer les lignes manquantes pour le BC
                            for (LigneDemandeAchat lda : da.getLignes()) {
                                log.info("Création ligne BC pour article: {}", lda.getArticle().getNom());
                                LigneBonCommande lbc = new LigneBonCommande();
                                lbc.setBonCommande(bc);
                                lbc.setArticle(lda.getArticle());
                                lbc.setQuantite(lda.getQuantite());
                                lbc.setPrixUnitaire(lda.getPrixEstime());
                                lbc.setRemise(BigDecimal.ZERO);
                                ligneBonCommandeRepository.save(lbc);
                                lignes.add(lbc);
                            }
                            log.info("Auto-réparation terminée: {} lignes créées.", lignes.size());
                        } else {
                            log.warn("La DA source ID {} n'a aucune ligne !", bc.getDemandeAchat().getId());
                        }
                    } else {
                        log.error("Impossible de trouver la DA ID {} en base !", bc.getDemandeAchat().getId());
                    }
                } else {
                    log.warn("Le BC ID {} n'a pas de DA source associée.", bcId);
                }
            } else {
                log.error("Impossible de trouver le BC ID {} en base !", bcId);
            }
        }
        return lignes;
    }

    @Transactional
    public BonCommandeFournisseur updateBonCommande(int id, BonCommandeFournisseur bcDetails) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));

        if (!"brouillon".equals(bc.getStatut())) {
            throw new RuntimeException("Seul un BC en brouillon peut être modifié");
        }

        if (bcDetails.getFournisseurId() != null) {
            Fournisseur f = fournisseurRepository.findById(bcDetails.getFournisseurId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
            bc.setFournisseur(f);
        }

        bc.setDateLivraisonPrevue(bcDetails.getDateLivraisonPrevue());
        bc.setMontantTotal(bcDetails.getMontantTotal());

        return bonCommandeFournisseurRepository.save(bc);
    }

    @Transactional
    public void updateLignes(int bcId, List<LigneBonCommande> nouvellesLignes) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(bcId)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));

        if (!"brouillon".equals(bc.getStatut())) {
            throw new RuntimeException("Seules les lignes d'un BC en brouillon peuvent être modifiées");
        }

        for (LigneBonCommande nouvelle : nouvellesLignes) {
            LigneBonCommande existante = ligneBonCommandeRepository.findById(nouvelle.getId())
                    .orElseThrow(() -> new RuntimeException("Ligne BC non trouvée: " + nouvelle.getId()));

            existante.setQuantite(nouvelle.getQuantite());
            existante.setPrixUnitaire(nouvelle.getPrixUnitaire());
            existante.setRemise(nouvelle.getRemise());

            ligneBonCommandeRepository.save(existante);
        }
    }

    @Transactional
    public BonCommandeFournisseur validerBC(int id, int utilisateurId) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));

        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String statutActuel = bc.getStatut() != null ? bc.getStatut().toLowerCase() : "brouillon";

        // Cas 1 : Soumission par l'Acheteur
        if ("brouillon".equals(statutActuel)) {
            if (!hasRole(user, "Acheteur") && !hasRole(user, "Administrateur")) {
                throw new RuntimeException("Seul un Acheteur ou Admin peut soumettre le BC pour validation");
            }
            if (bc.getFournisseur() == null) {
                throw new RuntimeException("Veuillez sélectionner un fournisseur avant de soumettre le BC");
            }
            bc.setStatut("attente_validation");
            return bonCommandeFournisseurRepository.save(bc);
        }

        // Cas 2 : Validation par le Responsable Achats
        if ("attente_validation".equals(statutActuel)) {
            if (!hasRole(user, "Responsable Achats") && !hasRole(user, "Administrateur")) {
                throw new RuntimeException("Seul un Responsable Achats ou Admin peut valider ce BC à cette étape");
            }

            if (bc.getMontantTotal().compareTo(SEUIL_RESPONSABLE) >= 0) {
                bc.setStatut("attente_approbation_finale");
            } else {
                bc.setStatut("valide");
            }
            return bonCommandeFournisseurRepository.save(bc);
        }

        // Cas 3 : Approbation finale par DAF ou DG
        if ("attente_approbation_finale".equals(statutActuel)) {
            if (!hasRole(user, "DAF") && !hasRole(user, "DG") && !hasRole(user, "Administrateur")) {
                throw new RuntimeException("Seul le DAF, le DG ou un Admin peut effectuer l'approbation finale");
            }
            bc.setStatut("valide");
            return bonCommandeFournisseurRepository.save(bc);
        }

        throw new RuntimeException("Le BC ne peut pas être validé dans son état actuel : " + statutActuel);
    }

    @Transactional
    public BonCommandeFournisseur signalerLitige(int id, int utilisateurId) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!hasRole(user, "Responsable Achats") && !hasRole(user, "Administrateur")) {
            throw new RuntimeException("Seul un Responsable Achats peut signaler un litige");
        }

        bc.setStatut("litige");
        return bonCommandeFournisseurRepository.save(bc);
    }

    @Transactional
    public BonCommandeFournisseur leverLitige(int id, int utilisateurId) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!hasRole(user, "Responsable Achats") && !hasRole(user, "Administrateur")) {
            throw new RuntimeException("Seul un Responsable Achats peut lever un litige");
        }

        bc.setStatut("attente_validation");
        return bonCommandeFournisseurRepository.save(bc);
    }

    @Transactional
    public BonCommandeFournisseur envoyerAuFournisseur(int id, int utilisateurId) {
        BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!hasRole(user, "Acheteur") && !hasRole(user, "Administrateur")) {
            throw new RuntimeException("Seul un Acheteur ou Admin peut envoyer le BC au fournisseur");
        }

        if (!"valide".equals(bc.getStatut().toLowerCase())) {
            throw new RuntimeException(
                    "Seul un BC validé peut être envoyé au fournisseur. Statut actuel: " + bc.getStatut());
        }

        bc.setStatut("envoye");
        BonCommandeFournisseur savedBc = bonCommandeFournisseurRepository.save(bc);

        // Journalisation pour la traçabilité
        journalAuditService.logAction(
                user,
                "ENVOI_BC_FOURNISSEUR",
                "ACHATS",
                bc.getReference(),
                "Bon de commande envoyé au fournisseur " + bc.getFournisseur().getNom() + " par " + user.getNom());

        return savedBc;
    }

    private boolean hasRole(Utilisateur user, String roleNom) {
        if (user.getRoles() == null)
            return false;
        return user.getRoles().stream()
                .anyMatch(r -> r.getNom().equalsIgnoreCase(roleNom));
    }
}
