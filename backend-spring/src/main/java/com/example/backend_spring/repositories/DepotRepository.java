package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Integer> {
    Optional<Depot> findByCode(String code);
}
