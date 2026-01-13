package com.example.backend_spring.dto;

import java.util.List;

public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private List<Integer> roleIds;
    private Integer departementId;

    public RegisterRequest() {
    }

    public RegisterRequest(String nom, String prenom, String email, String motDePasse, List<Integer> roleIds,
            Integer departementId) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roleIds = roleIds;
        this.departementId = departementId;
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }
}
