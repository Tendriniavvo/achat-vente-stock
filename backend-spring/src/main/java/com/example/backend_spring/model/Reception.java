package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receptions")
public class Reception {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "bon_commande_id")
    private BonCommandeFournisseur bonCommande;

    @Column(name = "date_reception")
    private LocalDateTime dateReception = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(length = 50)
    private String statut = "partielle";

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "reception", cascade = CascadeType.ALL)
    private List<LigneReception> lignes = new ArrayList<>();

    // Constructeurs
    public Reception() {
    }

    public Reception(String reference, BonCommandeFournisseur bonCommande) {
        this.reference = reference;
        this.bonCommande = bonCommande;
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

    public LocalDateTime getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDateTime dateReception) {
        this.dateReception = dateReception;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<LigneReception> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneReception> lignes) {
        this.lignes = lignes;
    }
}
