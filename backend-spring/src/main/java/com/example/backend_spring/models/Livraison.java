package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraisons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "commande_client_id")
    private CommandeClient commandeClient;

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @Column(length = 50)
    private String statut = "preparation"; // preparation, picking, expediee

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
