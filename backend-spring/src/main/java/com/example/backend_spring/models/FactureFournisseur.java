package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures_fournisseur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "bon_commande_id")
    private BonCommandeFournisseur bonCommande;

    @ManyToOne
    @JoinColumn(name = "reception_id")
    private Reception reception;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @Column(name = "date_facture")
    private LocalDateTime dateFacture;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(length = 50)
    private String statut = "attente"; // attente, validee, bloquee, payee

    @Column(columnDefinition = "TEXT")
    private String ecarts;

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;
}
