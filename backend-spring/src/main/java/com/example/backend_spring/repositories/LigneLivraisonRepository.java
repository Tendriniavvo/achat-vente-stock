package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneLivraisonRepository extends JpaRepository<LigneLivraison, Integer> {
}
