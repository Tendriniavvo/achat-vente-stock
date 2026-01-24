package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 255)
    private String path;

    @Column(length = 255)
    private String perimetre;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
