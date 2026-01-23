package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String telephone;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    private boolean actif = true;

    @Column(columnDefinition = "TEXT")
    private String historique;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
