package com.example.backend_spring.dto.stock;

import com.example.backend_spring.model.Depot;
import java.time.LocalDateTime;

public class DepotResponse {
    private Long id;
    private String nom;
    private String adresse;
    private Integer capacite;
    private Boolean actif;
    private LocalDateTime dateCreation;

    // Constructeurs
    public DepotResponse() {
    }

    public DepotResponse(Depot depot) {
        this.id = depot.getId() != null ? depot.getId().longValue() : null;
        this.nom = depot.getNom();
        this.adresse = depot.getAdresse();
        this.capacite = depot.getCapacite();
        this.actif = depot.getActif();
        // Note: dateCreation might not be in the current model, skip if not available
    }

    public DepotResponse(Long id, String nom, String adresse, Integer capacite, Boolean actif,
            LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.capacite = capacite;
        this.actif = actif;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
