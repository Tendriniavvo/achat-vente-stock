package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneBonCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBonCommandeRepository extends JpaRepository<LigneBonCommande, Integer> {
    List<LigneBonCommande> findByBonCommandeId(int bonCommandeId);
}
