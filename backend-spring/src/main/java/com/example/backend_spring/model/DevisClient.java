package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devis_clients")
public class DevisClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "date_devis")
    private LocalDateTime dateDevis = LocalDateTime.now();

    @Column(name = "date_validite")
    private LocalDateTime dateValidite;

    @Column(length = 50)
    private String statut = "brouillon";

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "remise_exceptionnelle")
    private Boolean remiseExceptionnelle = false;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL)
    private List<LigneDevis> lignes = new ArrayList<>();

    // Constructeurs
    public DevisClient() {
    }

    public DevisClient(String reference, Client client) {
        this.reference = reference;
        this.client = client;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDateDevis() {
        return dateDevis;
    }

    public void setDateDevis(LocalDateTime dateDevis) {
        this.dateDevis = dateDevis;
    }

    public LocalDateTime getDateValidite() {
        return dateValidite;
    }

    public void setDateValidite(LocalDateTime dateValidite) {
        this.dateValidite = dateValidite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Boolean getRemiseExceptionnelle() {
        return remiseExceptionnelle;
    }

    public void setRemiseExceptionnelle(Boolean remiseExceptionnelle) {
        this.remiseExceptionnelle = remiseExceptionnelle;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<LigneDevis> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneDevis> lignes) {
        this.lignes = lignes;
    }
}
