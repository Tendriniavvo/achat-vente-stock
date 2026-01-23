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
public class BonCommandeFournisseurService {

    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
    private final DemandeAchatRepository demandeAchatRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final LigneBonCommandeRepository ligneBonCommandeRepository;

    public List<BonCommandeFournisseur> getAllBonCommandes() {
        return bonCommandeFournisseurRepository.findAll();
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
    public BonCommandeFournisseur transformerEnBonCommande(int demandeAchatId, int acheteurId, Integer fournisseurId) {
        DemandeAchat demande = demandeAchatRepository.findById(demandeAchatId)
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
        long count = bonCommandeFournisseurRepository.count() + 1;
        int annee = LocalDateTime.now().getYear();
        return String.format("BC-%d-%04d", annee, count);
    }
}
