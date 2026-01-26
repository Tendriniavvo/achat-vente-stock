package com.example.backend_spring.services;

import com.example.backend_spring.dto.DemandeAchatDTO;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.DemandeAchat;
import com.example.backend_spring.models.LigneDemandeAchat;
import com.example.backend_spring.models.Stock;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.ArticleRepository;
import com.example.backend_spring.repositories.DemandeAchatRepository;
import com.example.backend_spring.repositories.LigneDemandeAchatRepository;
import com.example.backend_spring.repositories.StockRepository;
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
    private final StockRepository stockRepository;

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

        String statutActuel = demande.getStatut() != null ? demande.getStatut().toLowerCase() : "";
        System.out.println("DEBUG: Approuver ID=" + id + ", Statut=" + statutActuel
                + ", Validateur=" + validateur.getNom() + " (ID=" + validateurId + ")");

        if ("en attente".equals(statutActuel)) {
            // Approbation par le rôle "Approbateur" (ou Admin)
            if (!hasRole(validateur, "Approbateur") && !hasRole(validateur, "Administrateur")) {
                throw new RuntimeException("Seul un Approbateur ou Administrateur peut approuver la demande d'achat");
            }

            // ABAC : L'approbateur ne peut approuver que pour son propre département (si
            // pas admin)
            if (!hasRole(validateur, "Administrateur")) {
                if (demande.getDemandeur() != null && demande.getDemandeur().getDepartement() != null &&
                        validateur.getDepartement() != null &&
                        demande.getDemandeur().getDepartement().getId() != validateur.getDepartement().getId()) {
                    throw new RuntimeException(
                            "Un validateur ne peut approuver que les demandes de son propre département");
                }
            }

            // Vérification de la disponibilité en stock
            boolean dispoStock = verifierDisponibiliteStock(demande);

            if (dispoStock) {
                return updateStatusWithValidateur(id, "disponible_en_stock",
                        "Approbation validée par " + validateur.getNom() + ". L'article est disponible en stock.",
                        validateur);
            } else {
                return updateStatusWithValidateur(id, "attente_finance",
                        "Approbation validée par " + validateur.getNom()
                                + ". Article non disponible en stock. En attente de confirmation des fonds.",
                        validateur);
            }
        } else {
            throw new RuntimeException(
                    "Cette demande ne peut pas être approuvée dans son état actuel : " + statutActuel);
        }
    }

    private boolean verifierDisponibiliteStock(DemandeAchat demande) {
        if (demande.getLignes() == null || demande.getLignes().isEmpty()) {
            return false;
        }

        for (LigneDemandeAchat ligne : demande.getLignes()) {
            if (ligne.getArticle() == null)
                continue;

            List<Stock> stocks = stockRepository.findByArticleId(ligne.getArticle().getId());
            int quantiteTotale = stocks.stream().mapToInt(Stock::getQuantite).sum();

            // Si un seul article de la DA n'est pas disponible en quantité suffisante, on
            // considère que la DA doit suivre le flux d'achat
            if (quantiteTotale < ligne.getQuantite()) {
                return false;
            }
        }

        return true;
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
    public DemandeAchat verifierFonds(int id, int validateurId) {
        DemandeAchat demande = demandeAchatRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        Utilisateur validateur = utilisateurRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        // Vérification du rôle Finance (ou Admin)
        if (!hasRole(validateur, "Finance") && !hasRole(validateur, "Administrateur")
                && !hasRole(validateur, "Comptable")) {
            throw new RuntimeException(
                    "Seul un membre du service Finance ou un Administrateur peut confirmer les fonds");
        }

        // La vérification des fonds ne peut se faire que si la DA est en attente
        // Finance (après approbation)
        if (!demande.getStatut().equals("attente_finance")) {
            throw new RuntimeException("La demande doit être approuvée avant la vérification des fonds");
        }

        BigDecimal total = calculerTotalDemande(id);
        Utilisateur demandeur = demande.getDemandeur();

        if (demandeur == null || demandeur.getDepartement() == null) {
            throw new RuntimeException("Demandeur ou département non défini pour cette demande");
        }

        boolean disponibles = budgetService.isFondsDisponibles(demandeur.getDepartement(), total);

        if (!disponibles) {
            // Blocage si fonds insuffisants : on rejette pour bloquer le flux.
            updateStatusWithValidateur(id, "rejeté",
                    "Fonds budgétaires insuffisants pour le département " + demandeur.getDepartement().getNom() +
                            " (Total requis: " + formatCurrencyMGA(total) + ")",
                    validateur);
            throw new RuntimeException(
                    "Fonds insuffisants : le budget disponible est inférieur au montant total de la demande");
        }

        return updateStatusWithValidateur(id, "approuvé",
                "Disponibilité des fonds confirmée par " + validateur.getNom() + " (Total: " + formatCurrencyMGA(total)
                        + "). Demande prête pour transformation en BC.",
                validateur);
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
