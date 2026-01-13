package com.example.backend_spring.dto.security;

public class UtilisateurDto {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private Integer departementId;
    private String departementNom;

    // Constructeurs
    public UtilisateurDto() {
    }

    public UtilisateurDto(Integer id, String nom, String prenom, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public UtilisateurDto(Integer id, String nom, String prenom, String email, Integer departementId,
            String departementNom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.departementId = departementId;
        this.departementNom = departementNom;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public String getDepartementNom() {
        return departementNom;
    }

    public void setDepartementNom(String departementNom) {
        this.departementNom = departementNom;
    }
}
