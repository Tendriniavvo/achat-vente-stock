package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emplacements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emplacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Emplacement parent;

    @Column(nullable = false, length = 50)
    private String code;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TypeEmplacement typeEmplacement;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer capacite;

    @Column(columnDefinition = "TEXT")
    private String caracteristiques;

    @Column(columnDefinition = "TEXT")
    private String conditionsStockage;

    @Column(columnDefinition = "TEXT")
    private String typesProduitsAcceptes;
}
