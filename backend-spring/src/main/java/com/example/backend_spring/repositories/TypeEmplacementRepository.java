package com.example.backend_spring.repositories;

import com.example.backend_spring.models.TypeEmplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeEmplacementRepository extends JpaRepository<TypeEmplacement, Integer> {
    Optional<TypeEmplacement> findByLibelle(String libelle);
}
