package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures_clients")
public class FactureClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "commande_client_id")
    private CommandeClient commandeClient;

    @ManyToOne
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "date_facture")
    private LocalDateTime dateFacture;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(length = 50)
    private String statut = "attente";

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<LigneFactureClient> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "factureClient", cascade = CascadeType.ALL)
    private List<Encaissement> encaissements = new ArrayList<>();

    // Constructeurs
    public FactureClient() {
    }

    public FactureClient(String reference, Client client) {
        this.reference = reference;
        this.client = client;
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

    public CommandeClient getCommandeClient() {
        return commandeClient;
    }

    public void setCommandeClient(CommandeClient commandeClient) {
        this.commandeClient = commandeClient;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateurValidation() {
        return utilisateurValidation;
    }

    public void setUtilisateurValidation(Utilisateur utilisateurValidation) {
        this.utilisateurValidation = utilisateurValidation;
    }

    public List<LigneFactureClient> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFactureClient> lignes) {
        this.lignes = lignes;
    }

    public List<Encaissement> getEncaissements() {
        return encaissements;
    }

    public void setEncaissements(List<Encaissement> encaissements) {
        this.encaissements = encaissements;
    }
}
