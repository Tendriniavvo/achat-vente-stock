package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demandes_achat")
public class DemandeAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Utilisateur demandeur;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(length = 50)
    private String statut = "brouillon";

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @Column(name = "historique_validations", columnDefinition = "TEXT")
    private String historiqueValidations;

    @OneToMany(mappedBy = "demandeAchat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDemandeAchat> lignes = new ArrayList<>();

    // Constructeurs
    public DemandeAchat() {
    }

    public DemandeAchat(String reference, Utilisateur demandeur) {
        this.reference = reference;
        this.demandeur = demandeur;
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

    public Utilisateur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public String getHistoriqueValidations() {
        return historiqueValidations;
    }

    public void setHistoriqueValidations(String historiqueValidations) {
        this.historiqueValidations = historiqueValidations;
    }

    public List<LigneDemandeAchat> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneDemandeAchat> lignes) {
        this.lignes = lignes;
    }
}
