package com.example.backend_spring.dto.stock;

public class DepotRequest {
    private String nom;
    private String adresse;
    private Integer capacite;
    private Boolean actif;

    // Constructeurs
    public DepotRequest() {
    }

    public DepotRequest(String nom, String adresse, Integer capacite, Boolean actif) {
        this.nom = nom;
        this.adresse = adresse;
        this.capacite = capacite;
        this.actif = actif;
    }

    // Getters et Setters
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
}
