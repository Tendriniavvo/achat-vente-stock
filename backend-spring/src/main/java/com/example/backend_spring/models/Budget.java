package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "departement_id", nullable = false)
    private Departement departement;

    @Column(nullable = false)
    private int annee;

    @Column(name = "montant_initial", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantInitial;

    @Column(name = "montant_consomme", precision = 15, scale = 2)
    private BigDecimal montantConsomme = BigDecimal.ZERO;

    @Column(name = "montant_disponible", precision = 15, scale = 2)
    private BigDecimal montantDisponible;

    @PrePersist
    @PreUpdate
    public void calculateDisponible() {
        if (montantInitial != null) {
            this.montantDisponible = montantInitial
                    .subtract(montantConsomme != null ? montantConsomme : BigDecimal.ZERO);
        }
    }
}
