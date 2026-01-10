package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 100)
    private String module;

    @Column(name = "reference_objet", length = 50)
    private String referenceObjet;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "date_action")
    private LocalDateTime dateAction = LocalDateTime.now();

    // Constructeurs
    public JournalAudit() {
    }

    public JournalAudit(Utilisateur utilisateur, String action, String module) {
        this.utilisateur = utilisateur;
        this.action = action;
        this.module = module;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getReferenceObjet() {
        return referenceObjet;
    }

    public void setReferenceObjet(String referenceObjet) {
        this.referenceObjet = referenceObjet;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
