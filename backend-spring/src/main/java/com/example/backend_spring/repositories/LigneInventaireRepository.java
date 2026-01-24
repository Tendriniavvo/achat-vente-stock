package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneInventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneInventaireRepository extends JpaRepository<LigneInventaire, Integer> {
}
