package com.example.backend_spring.repositories;

import com.example.backend_spring.models.AjustementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AjustementStockRepository extends JpaRepository<AjustementStock, Integer> {
    Optional<AjustementStock> findByReference(String reference);
}
