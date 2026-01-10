package com.example.backend_spring.dto;

public class AuthResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String message;
    private Boolean success;

    public AuthResponse() {
    }

    public AuthResponse(Integer id, String nom, String prenom, String email, String message, Boolean success) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.message = message;
        this.success = success;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
