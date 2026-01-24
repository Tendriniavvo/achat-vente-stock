package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bons_commande_fournisseur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonCommandeFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "demande_achat_id")
    private DemandeAchat demandeAchat;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande = LocalDateTime.now();

    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    @Column(length = 50)
    private String statut = "brouillon"; // brouillon, valide, envoye, recu, annule

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
