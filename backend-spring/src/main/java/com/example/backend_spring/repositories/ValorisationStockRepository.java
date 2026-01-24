package com.example.backend_spring.repositories;

import com.example.backend_spring.models.ValorisationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorisationStockRepository extends JpaRepository<ValorisationStock, Integer> {
}
