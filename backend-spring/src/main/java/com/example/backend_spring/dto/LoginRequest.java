package com.example.backend_spring.dto;

public class LoginRequest {
    private String email;
    private String motDePasse;
    private Boolean rememberMe;

    public LoginRequest() {
    }

    public LoginRequest(String email, String motDePasse, Boolean rememberMe) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.rememberMe = rememberMe;
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

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
