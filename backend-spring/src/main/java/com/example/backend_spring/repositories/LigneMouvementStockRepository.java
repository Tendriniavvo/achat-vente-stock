package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneMouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneMouvementStockRepository extends JpaRepository<LigneMouvementStock, Integer> {
}
