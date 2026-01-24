package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "receptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reception {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "bon_commande_id")
    private BonCommandeFournisseur bonCommande;

    @Column(name = "date_reception")
    private LocalDateTime dateReception = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(length = 50)
    private String statut = "partielle"; // partielle, complete, litige

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
