package com.example.backend_spring.services;

import com.example.backend_spring.dto.DemandeAchatDTO;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.DemandeAchat;
import com.example.backend_spring.models.LigneDemandeAchat;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.ArticleRepository;
import com.example.backend_spring.repositories.DemandeAchatRepository;
import com.example.backend_spring.repositories.LigneDemandeAchatRepository;
import com.example.backend_spring.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DemandeAchatService {
    private final DemandeAchatRepository demandeAchatRepository;
    private final LigneDemandeAchatRepository ligneDemandeAchatRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ArticleRepository articleRepository;
    private final BudgetService budgetService;

    private static final BigDecimal SEUIL_N2 = new BigDecimal("1000000");
    private static final BigDecimal SEUIL_N3 = new BigDecimal("5000000");

    @Transactional
    public DemandeAchat approuver(int id, int validateurId) {
        DemandeAchat demande = demandeAchatRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        Utilisateur validateur = utilisateurRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        // Séparation des tâches : le demandeur ne peut pas approuver sa propre DA
        if (demande.getDemandeur() != null && demande.getDemandeur().getId() == validateurId) {
            throw new RuntimeException("Un demandeur ne peut pas approuver sa propre demande d'achat");
        }

        BigDecimal total = calculerTotalDemande(id);
        String statutActuel = demande.getStatut() != null ? demande.getStatut().toLowerCase() : "";
        System.out.println("DEBUG: Approuver ID=" + id + ", Statut=" + statutActuel + ", Total=" + total
                + ", Validateur=" + validateur.getNom() + " (ID=" + validateurId + ")");

        if ("en attente".equals(statutActuel)) {
            // Approbation N1 (Acheteur / Chef)
            if (!hasRole(validateur, "Acheteur") && !hasRole(validateur, "Administrateur")) {
                System.out.println("DEBUG: Erreur N1 - Pas le rôle Acheteur ou Administrateur. Roles=" + validateur.getRoles());
                throw new RuntimeException("Seul un Acheteur ou Administrateur peut effectuer l'approbation N1");
            }

            // ABAC : L'acheteur ne peut approuver que pour son propre département (si pas admin)
            if (hasRole(validateur, "Acheteur") && !hasRole(validateur, "Administrateur")) {
                if (demande.getDemandeur() != null && demande.getDemandeur().getDepartement() != null &&
                        validateur.getDepartement() != null &&
                        demande.getDemandeur().getDepartement().getId() != validateur.getDepartement().getId()) {
                    System.out.println("DEBUG: Erreur N1 - Département différent. DemandeDept=" +
                            demande.getDemandeur().getDepartement().getId() + ", ValidateurDept="
                            + validateur.getDepartement().getId());
                    throw new RuntimeException("Un validateur ne peut approuver que les demandes de son propre département");
                }
            }

            System.out.println("DEBUG: N1 - Passage à attente_finance");
            return updateStatusWithValidateur(id, "attente_finance",
                    "Approbation N1 validée par " + validateur.getNom() + ". En attente de la Finance.",
                    validateur);

        } else if ("attente_finance".equals(statutActuel) || "fonds_confirmés".equals(statutActuel)
                || "fonds_confirmes".equals(statutActuel)) {
            // Approbation N2 (Comptable / Finance)
            if (!hasRole(validateur, "Comptable") && !hasRole(validateur, "Administrateur")) {
                System.out.println("DEBUG: Erreur N2 - Pas le rôle Comptable ou Administrateur");
                throw new RuntimeException("Seul un membre de la Comptabilité peut effectuer l'approbation N2");
            }

            // Exiger la vérification des fonds avant l'approbation N2
            if ("attente_finance".equals(statutActuel)) {
                System.out.println("DEBUG: Erreur N2 - Fonds non confirmés");
                throw new RuntimeException(
                        "La disponibilité des fonds doit être confirmée avant l'approbation Finance");
            }

            if (total.compareTo(SEUIL_N3) < 0) {
                // Consommer le budget lors de l'approbation finale
                System.out.println("DEBUG: N2 - Montant < SEUIL_N3. Approbation finale.");
                budgetService.consommerBudget(demande.getDemandeur().getDepartement(), total);

                return updateStatusWithValidateur(id, "approuvé",
                        "Approbation N2 (Finance) finalisée par " + validateur.getNom(), validateur);
            } else {
                System.out.println("DEBUG: N2 - Montant >= SEUIL_N3. Passage à attente_admin");
                return updateStatusWithValidateur(id, "attente_admin", "Approbation N2 (Finance) validée par "
                        + validateur.getNom() + ". En attente de l'Administration.", validateur);
            }
        } else if ("attente_admin".equals(statutActuel)) {
            // Approbation N3 (Administrateur)
            if (!hasRole(validateur, "Administrateur")) {
                throw new RuntimeException("Seul un Administrateur peut effectuer l'approbation N3");
            }

            // Si on vient de N3 et que le budget n'a pas été consommé en N2 (car SEUIL_N3
            // atteint)
            budgetService.consommerBudget(demande.getDemandeur().getDepartement(), total);

            return updateStatusWithValidateur(id, "approuvé",
                    "Approbation N3 (Admin) finalisée par " + validateur.getNom(), validateur);
        } else {
            throw new RuntimeException(
                    "Cette demande ne peut pas être approuvée dans son état actuel : " + statutActuel);
        }
    }

    private boolean hasRole(Utilisateur user, String roleNom) {
        if (user.getRoles() == null)
            return false;
        return user.getRoles().stream()
                .anyMatch(r -> r.getNom().equalsIgnoreCase(roleNom));
    }

    @Transactional
    public DemandeAchat updateStatusWithValidateur(int id, String newStatus, String message, Utilisateur validateur) {
        System.out.println("DEBUG: updateStatusWithValidateur ID=" + id + ", newStatus=" + newStatus + ", validateur="
                + validateur.getNom());
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        String oldStatus = demande.getStatut();
        demande.setStatut(newStatus);

        String historique = demande.getHistoriqueValidations();
        if (historique == null)
            historique = "";

        String validateurNom = (validateur != null) ? (validateur.getPrenom() + " " + validateur.getNom()) : "Système";
        historique += LocalDateTime.now() + " [" + validateurNom + "]: Passage de " + oldStatus + " à "
                + newStatus;
        if (message != null && !message.isEmpty()) {
            historique += " (" + message + ")";
        }
        historique += "\n";
        demande.setHistoriqueValidations(historique);

        DemandeAchat saved = demandeAchatRepository.save(demande);
        System.out.println("DEBUG: Sauvegarde réussie. Nouveau statut=" + saved.getStatut());
        return saved;
    }

    public List<DemandeAchat> getAllDemandes() {
        return demandeAchatRepository.findAllWithDetails();
    }

    public Optional<DemandeAchat> getDemandeById(int id) {
        return demandeAchatRepository.findByIdWithDetails(id);
    }

    public List<LigneDemandeAchat> getLignesByDemandeId(int id) {
        return ligneDemandeAchatRepository.findByDemandeAchatId(id);
    }

    public BigDecimal calculerTotalDemande(int id) {
        List<LigneDemandeAchat> lignes = getLignesByDemandeId(id);
        return lignes.stream()
                .map(l -> l.getPrixEstime().multiply(BigDecimal.valueOf(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public DemandeAchat verifierFonds(int id) {
        DemandeAchat demande = demandeAchatRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        // La vérification des fonds ne peut se faire que si la DA est en attente
        // Finance (après N1)
        if (!demande.getStatut().equals("attente_finance")) {
            throw new RuntimeException(
                    "La demande doit être validée par le Chef de département (N1) avant la vérification des fonds");
        }

        BigDecimal total = calculerTotalDemande(id);
        Utilisateur demandeur = demande.getDemandeur();

        if (demandeur == null || demandeur.getDepartement() == null) {
            throw new RuntimeException("Demandeur ou département non défini pour cette demande");
        }

        boolean disponibles = budgetService.isFondsDisponibles(demandeur.getDepartement(), total);

        if (!disponibles) {
            // Blocage si fonds insuffisants : on laisse en attente_finance mais avec un
            // message d'erreur
            // Ou on rejette si c'est définitif. Ici on choisit le rejet pour bloquer le
            // flux.
            updateStatus(id, "rejeté",
                    "Fonds budgétaires insuffisants pour le département " + demandeur.getDepartement().getNom() +
                            " (Total requis: " + formatCurrencyMGA(total) + ")");
            throw new RuntimeException(
                    "Fonds insuffisants : le budget disponible est inférieur au montant total de la demande");
        }

        return updateStatus(id, "fonds_confirmés",
                "Disponibilité des fonds confirmée par la Finance (Total: " + formatCurrencyMGA(total) + ")");
    }

    private String formatCurrencyMGA(BigDecimal amount) {
        return new java.text.DecimalFormat("#,### MGA").format(amount);
    }

    public Optional<DemandeAchat> getDemandeByReference(String reference) {
        return demandeAchatRepository.findByReference(reference);
    }

    @Transactional
    public DemandeAchat createDemande(DemandeAchatDTO dto) {
        DemandeAchat demande = new DemandeAchat();
        demande.setReference(dto.getReference());
        demande.setDateCreation(LocalDateTime.now());
        demande.setStatut("brouillon");

        if (dto.getDemandeurId() != null) {
            Utilisateur demandeur = utilisateurRepository.findById(dto.getDemandeurId())
                    .orElseThrow(() -> new RuntimeException("Demandeur non trouvé"));
            demande.setDemandeur(demandeur);
        }

        DemandeAchat savedDemande = demandeAchatRepository.save(demande);

        if (dto.getLignes() != null) {
            for (DemandeAchatDTO.LigneDemandeAchatDTO ligneDto : dto.getLignes()) {
                LigneDemandeAchat ligne = new LigneDemandeAchat();
                ligne.setDemandeAchat(savedDemande);

                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new RuntimeException("Article non trouvé: " + ligneDto.getArticleId()));

                ligne.setArticle(article);
                ligne.setQuantite(ligneDto.getQuantite());
                ligne.setPrixEstime(ligneDto.getPrixEstime());

                ligneDemandeAchatRepository.save(ligne);
            }
        }

        return savedDemande;
    }

    @Transactional
    public DemandeAchat updateStatus(int id, String newStatus, String motif) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        String oldStatus = demande.getStatut();
        demande.setStatut(newStatus);

        if (motif != null && !motif.isEmpty()) {
            demande.setMotifRejet(motif);
        }

        String historique = demande.getHistoriqueValidations();
        if (historique == null)
            historique = "";
        historique += LocalDateTime.now() + ": Passage de " + oldStatus + " à " + newStatus;
        if (motif != null && !motif.isEmpty()) {
            historique += " (Motif: " + motif + ")";
        }
        historique += "\n";
        demande.setHistoriqueValidations(historique);

        return demandeAchatRepository.save(demande);
    }

    @Transactional
    public DemandeAchat updateDemande(int id, DemandeAchatDTO dto) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!demande.getStatut().equals("brouillon")) {
            throw new RuntimeException("Seules les demandes en brouillon peuvent être modifiées");
        }

        demande.setReference(dto.getReference());

        // Supprimer les anciennes lignes
        List<LigneDemandeAchat> oldLignes = ligneDemandeAchatRepository.findByDemandeAchatId(id);
        ligneDemandeAchatRepository.deleteAll(oldLignes);

        // Ajouter les nouvelles lignes
        if (dto.getLignes() != null) {
            for (DemandeAchatDTO.LigneDemandeAchatDTO ligneDto : dto.getLignes()) {
                LigneDemandeAchat ligne = new LigneDemandeAchat();
                ligne.setDemandeAchat(demande);

                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new RuntimeException("Article non trouvé: " + ligneDto.getArticleId()));

                ligne.setArticle(article);
                ligne.setQuantite(ligneDto.getQuantite());
                ligne.setPrixEstime(ligneDto.getPrixEstime());

                ligneDemandeAchatRepository.save(ligne);
            }
        }

        return demandeAchatRepository.save(demande);
    }

    @Transactional
    public void deleteDemande(int id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!demande.getStatut().equals("brouillon")) {
            throw new RuntimeException("Seules les demandes en brouillon peuvent être supprimées");
        }

        // Supprimer les lignes d'abord
        List<LigneDemandeAchat> lignes = ligneDemandeAchatRepository.findByDemandeAchat(demande);
        ligneDemandeAchatRepository.deleteAll(lignes);

        demandeAchatRepository.delete(demande);
    }
}
