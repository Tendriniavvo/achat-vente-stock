package com.example.backend_spring.service.achat;

import com.example.backend_spring.dto.achat.DemandeAchatRequest;
import com.example.backend_spring.dto.achat.DemandeAchatResponse;
import com.example.backend_spring.dto.achat.LigneDemandeAchatDto;
import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.DemandeAchat;
import com.example.backend_spring.model.LigneDemandeAchat;
import com.example.backend_spring.model.Utilisateur;
import com.example.backend_spring.repository.achat.DemandeAchatRepository;
import com.example.backend_spring.repository.article.ArticleRepository;
import com.example.backend_spring.repository.security.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemandeAchatService {

    private final DemandeAchatRepository demandeAchatRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ArticleRepository articleRepository;

    public DemandeAchatService(DemandeAchatRepository demandeAchatRepository,
            UtilisateurRepository utilisateurRepository,
            ArticleRepository articleRepository) {
        this.demandeAchatRepository = demandeAchatRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public DemandeAchatResponse createDemandeAchat(DemandeAchatRequest request) {
        // Récupérer l'utilisateur demandeur
        Utilisateur demandeur = utilisateurRepository.findById(request.getDemandeurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer la demande d'achat
        DemandeAchat demandeAchat = new DemandeAchat();
        demandeAchat.setReference(request.getReference());
        demandeAchat.setDemandeur(demandeur);
        demandeAchat.setStatut("brouillon");

        // Créer les lignes de demande
        List<LigneDemandeAchat> lignes = new ArrayList<>();
        for (LigneDemandeAchatDto ligneDto : request.getLignes()) {
            Article article = articleRepository.findById(ligneDto.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article non trouvé: " + ligneDto.getArticleId()));

            LigneDemandeAchat ligne = new LigneDemandeAchat();
            ligne.setDemandeAchat(demandeAchat);
            ligne.setArticle(article);
            ligne.setQuantite(ligneDto.getQuantite());
            ligne.setPrixEstime(ligneDto.getPrixEstime());
            lignes.add(ligne);
        }
        demandeAchat.setLignes(lignes);

        // Sauvegarder
        DemandeAchat savedDemande = demandeAchatRepository.save(demandeAchat);

        // Convertir en réponse
        return toResponse(savedDemande);
    }

    public List<DemandeAchatResponse> getAllDemandesAchat() {
        List<DemandeAchat> demandes = demandeAchatRepository.findAll();
        return demandes.stream().map(this::toResponse).toList();
    }

    public DemandeAchatResponse getDemandeAchatById(Integer id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));
        return toResponse(demande);
    }

    @Transactional
    public DemandeAchatResponse updateDemandeAchat(Integer id, DemandeAchatRequest request) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        // Vérifier que la demande est modifiable (statut brouillon)
        if (!"brouillon".equals(demande.getStatut())) {
            throw new RuntimeException("Seules les demandes en brouillon peuvent être modifiées");
        }

        // Mettre à jour la référence
        demande.setReference(request.getReference());

        // Mettre à jour le demandeur si nécessaire
        if (request.getDemandeurId() != null) {
            Utilisateur demandeur = utilisateurRepository.findById(request.getDemandeurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            demande.setDemandeur(demandeur);
        }

        // Supprimer les anciennes lignes et ajouter les nouvelles sans réassigner la
        // collection
        demande.getLignes().clear();
        for (LigneDemandeAchatDto ligneDto : request.getLignes()) {
            Article article = articleRepository.findById(ligneDto.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article non trouvé: " + ligneDto.getArticleId()));

            LigneDemandeAchat ligne = new LigneDemandeAchat();
            ligne.setDemandeAchat(demande);
            ligne.setArticle(article);
            ligne.setQuantite(ligneDto.getQuantite());
            ligne.setPrixEstime(ligneDto.getPrixEstime());
            demande.getLignes().add(ligne);
        }

        DemandeAchat updatedDemande = demandeAchatRepository.save(demande);
        return toResponse(updatedDemande);
    }

    @Transactional
    public DemandeAchatResponse soumettreValidation(Integer id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!"brouillon".equals(demande.getStatut())) {
            throw new RuntimeException("Seules les demandes en brouillon peuvent être soumises");
        }

        if (demande.getLignes().isEmpty()) {
            throw new RuntimeException("Impossible de soumettre une demande sans articles");
        }

        demande.setStatut("soumise");
        // Ajouter dans l'historique
        String historique = demande.getHistoriqueValidations() == null ? "" : demande.getHistoriqueValidations();
        historique += String.format("[%s] Soumise pour validation\n", java.time.LocalDateTime.now());
        demande.setHistoriqueValidations(historique);

        DemandeAchat updatedDemande = demandeAchatRepository.save(demande);
        return toResponse(updatedDemande);
    }

    @Transactional
    public DemandeAchatResponse approuverDemandeAchat(Integer id, Integer validateurId) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!"soumise".equals(demande.getStatut())) {
            throw new RuntimeException("Seules les demandes soumises peuvent être approuvées");
        }

        Utilisateur validateur = utilisateurRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        demande.setStatut("validee");
        String historique = demande.getHistoriqueValidations() == null ? "" : demande.getHistoriqueValidations();
        historique += String.format("[%s] Approuvée par %s %s\n",
                java.time.LocalDateTime.now(),
                validateur.getPrenom(),
                validateur.getNom());
        demande.setHistoriqueValidations(historique);

        DemandeAchat updatedDemande = demandeAchatRepository.save(demande);
        return toResponse(updatedDemande);
    }

    @Transactional
    public DemandeAchatResponse rejeterDemandeAchat(Integer id, Integer validateurId, String motif) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!"soumise".equals(demande.getStatut())) {
            throw new RuntimeException("Seules les demandes soumises peuvent être rejetées");
        }

        Utilisateur validateur = utilisateurRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        demande.setStatut("rejetee");
        String historique = demande.getHistoriqueValidations() == null ? "" : demande.getHistoriqueValidations();
        historique += String.format("[%s] Rejetée par %s %s - Motif: %s\n",
                java.time.LocalDateTime.now(),
                validateur.getPrenom(),
                validateur.getNom(),
                motif != null ? motif : "Non spécifié");
        demande.setHistoriqueValidations(historique);

        DemandeAchat updatedDemande = demandeAchatRepository.save(demande);
        return toResponse(updatedDemande);
    }

    @Transactional
    public void annulerDemandeAchat(Integer id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if ("validee".equals(demande.getStatut())) {
            throw new RuntimeException("Impossible d'annuler une demande validée");
        }

        demande.setStatut("annulee");
        String historique = demande.getHistoriqueValidations() == null ? "" : demande.getHistoriqueValidations();
        historique += String.format("[%s] Annulée\n", java.time.LocalDateTime.now());
        demande.setHistoriqueValidations(historique);

        demandeAchatRepository.save(demande);
    }

    @Transactional
    public void deleteDemandeAchat(Integer id) {
        DemandeAchat demande = demandeAchatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

        if (!"brouillon".equals(demande.getStatut()) && !"annulee".equals(demande.getStatut())) {
            throw new RuntimeException("Seules les demandes en brouillon ou annulées peuvent être supprimées");
        }

        demandeAchatRepository.delete(demande);
    }

    private DemandeAchatResponse toResponse(DemandeAchat demande) {
        List<LigneDemandeAchatDto> lignesDto = demande.getLignes().stream()
                .map(ligne -> new LigneDemandeAchatDto(
                        ligne.getArticle().getId(),
                        ligne.getQuantite(),
                        ligne.getPrixEstime()))
                .toList();

        return new DemandeAchatResponse(
                demande.getId(),
                demande.getReference(),
                demande.getDemandeur().getId(),
                demande.getDemandeur().getNom(),
                demande.getDemandeur().getPrenom(),
                demande.getDateCreation(),
                demande.getStatut(),
                demande.getHistoriqueValidations(),
                lignesDto);
    }
}
