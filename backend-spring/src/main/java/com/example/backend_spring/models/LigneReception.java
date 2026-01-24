package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lignes_receptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneReception {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "reception_id")
    private Reception reception;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_recue", nullable = false)
    private int quantiteRecue;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(columnDefinition = "TEXT")
    private String ecart;
}
