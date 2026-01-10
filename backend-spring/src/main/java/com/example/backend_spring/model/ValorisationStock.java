package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "valorisations_stock")
public class ValorisationStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_valorisation", nullable = false)
    private LocalDateTime dateValorisation;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(name = "valeur_totale", precision = 10, scale = 2)
    private BigDecimal valeurTotale;

    @Column(precision = 10, scale = 2)
    private BigDecimal ecarts;

    @Column(columnDefinition = "TEXT")
    private String rapport;

    // Constructeurs
    public ValorisationStock() {
    }

    public ValorisationStock(LocalDateTime dateValorisation, Depot depot) {
        this.dateValorisation = dateValorisation;
        this.depot = depot;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateValorisation() {
        return dateValorisation;
    }

    public void setDateValorisation(LocalDateTime dateValorisation) {
        this.dateValorisation = dateValorisation;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public BigDecimal getValeurTotale() {
        return valeurTotale;
    }

    public void setValeurTotale(BigDecimal valeurTotale) {
        this.valeurTotale = valeurTotale;
    }

    public BigDecimal getEcarts() {
        return ecarts;
    }

    public void setEcarts(BigDecimal ecarts) {
        this.ecarts = ecarts;
    }

    public String getRapport() {
        return rapport;
    }

    public void setRapport(String rapport) {
        this.rapport = rapport;
    }
}
