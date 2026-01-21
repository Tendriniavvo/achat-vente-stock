package com.example.backend_spring.service.achat;

import com.example.backend_spring.dto.achat.BonCommandeFournisseurResponse;
import com.example.backend_spring.dto.achat.LigneBonCommandeDto;
import com.example.backend_spring.model.BonCommandeFournisseur;
import com.example.backend_spring.model.DemandeAchat;
import com.example.backend_spring.model.Fournisseur;
import com.example.backend_spring.model.LigneBonCommande;
import com.example.backend_spring.model.LigneDemandeAchat;
import com.example.backend_spring.model.Utilisateur;
import com.example.backend_spring.repository.achat.BonCommandeFournisseurRepository;
import com.example.backend_spring.repository.achat.DemandeAchatRepository;
import com.example.backend_spring.repository.partenaire.FournisseurRepository;
import com.example.backend_spring.repository.security.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BonCommandeFournisseurService {

        private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
        private final DemandeAchatRepository demandeAchatRepository;
        private final FournisseurRepository fournisseurRepository;
        private final UtilisateurRepository utilisateurRepository;

        public BonCommandeFournisseurService(BonCommandeFournisseurRepository bonCommandeFournisseurRepository,
                        DemandeAchatRepository demandeAchatRepository,
                        FournisseurRepository fournisseurRepository,
                        UtilisateurRepository utilisateurRepository) {
                this.bonCommandeFournisseurRepository = bonCommandeFournisseurRepository;
                this.demandeAchatRepository = demandeAchatRepository;
                this.fournisseurRepository = fournisseurRepository;
                this.utilisateurRepository = utilisateurRepository;
        }

        @Transactional
        public BonCommandeFournisseurResponse genererBonCommandeDepuisDA(Integer demandeAchatId, Integer fournisseurId,
                        Integer utilisateurId) {
                // 1. Récupérer la demande d'achat
                DemandeAchat demandeAchat = demandeAchatRepository.findById(demandeAchatId)
                                .orElseThrow(() -> new RuntimeException("Demande d'achat non trouvée"));

                // 2. Vérifier que la DA est validée (selon la logique métier, une DA doit être
                // validée avant de devenir un BC)
                if (!"validee".equals(demandeAchat.getStatut())) {
                        throw new RuntimeException(
                                        "La demande d'achat doit être validée pour générer un bon de commande");
                }

                // Vérifier s'il existe déjà un BC pour cette DA
                // Note: La relation est OneToMany ou ManyToOne selon le modèle, ici on suppose
                // qu'une DA peut générer un BC
                // Si vous voulez une contrainte 1-1 stricte, il faudrait vérifier dans le repo.
                // Ici on autorise la génération mais on pourrait ajouter un check.

                // 3. Récupérer le fournisseur
                Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

                // 4. Récupérer l'utilisateur créateur
                Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                // 5. Créer le Bon de Commande
                BonCommandeFournisseur bc = new BonCommandeFournisseur();
                bc.setReference("BC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Génération auto
                                                                                                     // référence
                bc.setDemandeAchat(demandeAchat);
                bc.setFournisseur(fournisseur);
                bc.setUtilisateur(utilisateur);
                bc.setDateCommande(LocalDateTime.now());
                bc.setStatut("brouillon");

                // 6. Convertir les lignes de DA en lignes de BC
                List<LigneBonCommande> lignesBC = new ArrayList<>();
                BigDecimal montantTotal = BigDecimal.ZERO;

                for (LigneDemandeAchat ligneDA : demandeAchat.getLignes()) {
                        LigneBonCommande ligneBC = new LigneBonCommande();
                        ligneBC.setBonCommande(bc);
                        ligneBC.setArticle(ligneDA.getArticle());
                        ligneBC.setQuantite(ligneDA.getQuantite());

                        // Prix unitaire par défaut (prix estimé ou 0)
                        BigDecimal prixUnitaire = ligneDA.getPrixEstime() != null ? ligneDA.getPrixEstime()
                                        : BigDecimal.ZERO;
                        ligneBC.setPrixUnitaire(prixUnitaire);
                        ligneBC.setRemise(BigDecimal.ZERO);

                        // Calcul montant ligne
                        BigDecimal montantLigne = prixUnitaire.multiply(BigDecimal.valueOf(ligneDA.getQuantite()));
                        montantTotal = montantTotal.add(montantLigne);

                        lignesBC.add(ligneBC);
                }

                bc.setLignes(lignesBC);
                bc.setMontantTotal(montantTotal);

                // 7. Sauvegarder
                BonCommandeFournisseur savedBC = bonCommandeFournisseurRepository.save(bc);

                return toResponse(savedBC);
        }

        public List<BonCommandeFournisseurResponse> getAllBonsCommande() {
                return bonCommandeFournisseurRepository.findAll().stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());
        }

        public BonCommandeFournisseurResponse getBonCommandeById(Integer id) {
                BonCommandeFournisseur bc = bonCommandeFournisseurRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Bon de commande non trouvé"));
                return toResponse(bc);
        }

        private BonCommandeFournisseurResponse toResponse(BonCommandeFournisseur bc) {
                List<LigneBonCommandeDto> lignesDto = bc.getLignes().stream()
                                .map(l -> new LigneBonCommandeDto(
                                                l.getId(),
                                                l.getArticle().getId(),
                                                l.getArticle().getNom(),
                                                l.getQuantite(),
                                                l.getPrixUnitaire(),
                                                l.getRemise()))
                                .collect(Collectors.toList());

                return new BonCommandeFournisseurResponse(
                                bc.getId(),
                                bc.getReference(),
                                bc.getDemandeAchat() != null ? bc.getDemandeAchat().getId() : null,
                                bc.getDemandeAchat() != null ? bc.getDemandeAchat().getReference() : null,
                                bc.getFournisseur().getId(),
                                bc.getFournisseur().getNom(),
                                bc.getDateCommande(),
                                bc.getDateLivraisonPrevue(),
                                bc.getStatut(),
                                bc.getMontantTotal(),
                                bc.getUtilisateur() != null
                                                ? bc.getUtilisateur().getNom() + " " + bc.getUtilisateur().getPrenom()
                                                : null,
                                lignesDto);
        }
}
