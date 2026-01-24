package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 100)
    private String action; // Ex: creation, modification, validation

    @Column(length = 100)
    private String module;

    @Column(name = "reference_objet", length = 50)
    private String referenceObjet;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "date_action")
    private LocalDateTime dateAction = LocalDateTime.now();
}
