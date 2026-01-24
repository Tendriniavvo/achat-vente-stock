package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "depots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Depot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    private String responsable;

    private Integer capacite;

    private String typeEntreposage;

    private String horairesOuverture;

    private boolean actif = true;
}
