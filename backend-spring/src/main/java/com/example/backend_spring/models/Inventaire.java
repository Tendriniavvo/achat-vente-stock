package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(length = 50)
    private String type = "annuel"; // annuel, tournant

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(length = 50)
    private String statut = "en_cours"; // en_cours, termine, valide

    @ManyToOne
    @JoinColumn(name = "utilisateur_lancement")
    private Utilisateur utilisateurLancement;
}
