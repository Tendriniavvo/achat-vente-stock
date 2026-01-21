package com.example.backend_spring.dto.security;

public class DepartementDto {
    private Integer id;
    private String code;
    private String nom;
    private String description;
    private Boolean actif;

    // Constructeurs
    public DepartementDto() {
    }

    public DepartementDto(Integer id, String code, String nom, String description, Boolean actif) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.actif = actif;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
