package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventaires")
public class Inventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(length = 50)
    private String type = "annuel";

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(length = 50)
    private String statut = "en_cours";

    @ManyToOne
    @JoinColumn(name = "utilisateur_lancement")
    private Utilisateur utilisateurLancement;

    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL)
    private List<LigneInventaire> lignes = new ArrayList<>();

    // Constructeurs
    public Inventaire() {
    }

    public Inventaire(String reference, String type) {
        this.reference = reference;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
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

    public Utilisateur getUtilisateurLancement() {
        return utilisateurLancement;
    }

    public void setUtilisateurLancement(Utilisateur utilisateurLancement) {
        this.utilisateurLancement = utilisateurLancement;
    }

    public List<LigneInventaire> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneInventaire> lignes) {
        this.lignes = lignes;
    }
}
