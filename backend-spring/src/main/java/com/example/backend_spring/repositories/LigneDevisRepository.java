package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneDevisRepository extends JpaRepository<LigneDevis, Integer> {
}
