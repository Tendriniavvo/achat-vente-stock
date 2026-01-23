package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "valorisations_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorisationStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
}
