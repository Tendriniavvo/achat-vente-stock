package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "encaissements")
public class Encaissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "facture_client_id")
    private FactureClient factureClient;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_encaissement")
    private LocalDateTime dateEncaissement = LocalDateTime.now();

    @Column(name = "mode_paiement", length = 50)
    private String modePaiement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // Constructeurs
    public Encaissement() {
    }

    public Encaissement(FactureClient factureClient, BigDecimal montant, String modePaiement) {
        this.factureClient = factureClient;
        this.montant = montant;
        this.modePaiement = modePaiement;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FactureClient getFactureClient() {
        return factureClient;
    }

    public void setFactureClient(FactureClient factureClient) {
        this.factureClient = factureClient;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateEncaissement() {
        return dateEncaissement;
    }

    public void setDateEncaissement(LocalDateTime dateEncaissement) {
        this.dateEncaissement = dateEncaissement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
