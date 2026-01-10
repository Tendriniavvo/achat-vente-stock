package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures_fournisseur")
public class FactureFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
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
    private String statut = "attente";

    @Column(columnDefinition = "TEXT")
    private String ecarts;

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<LigneFactureFournisseur> lignes = new ArrayList<>();

    // Constructeurs
    public FactureFournisseur() {
    }

    public FactureFournisseur(String reference, Fournisseur fournisseur) {
        this.reference = reference;
        this.fournisseur = fournisseur;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BonCommandeFournisseur getBonCommande() {
        return bonCommande;
    }

    public void setBonCommande(BonCommandeFournisseur bonCommande) {
        this.bonCommande = bonCommande;
    }

    public Reception getReception() {
        return reception;
    }

    public void setReception(Reception reception) {
        this.reception = reception;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getEcarts() {
        return ecarts;
    }

    public void setEcarts(String ecarts) {
        this.ecarts = ecarts;
    }

    public Utilisateur getUtilisateurValidation() {
        return utilisateurValidation;
    }

    public void setUtilisateurValidation(Utilisateur utilisateurValidation) {
        this.utilisateurValidation = utilisateurValidation;
    }

    public List<LigneFactureFournisseur> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFactureFournisseur> lignes) {
        this.lignes = lignes;
    }
}
