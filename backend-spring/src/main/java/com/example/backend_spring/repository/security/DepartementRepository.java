package com.example.backend_spring.repository.security;

import com.example.backend_spring.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    Optional<Departement> findByCode(String code);

    List<Departement> findByActif(Boolean actif);

    List<Departement> findByNomContainingIgnoreCase(String nom);
}
