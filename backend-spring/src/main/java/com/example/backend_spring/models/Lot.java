package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "numero_lot", unique = true, nullable = false, length = 50)
    private String numeroLot;

    @Column(name = "date_entree")
    private LocalDateTime dateEntree;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    private Integer quantite;

    private boolean conforme = true;
}
