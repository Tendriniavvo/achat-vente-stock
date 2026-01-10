package com.example.backend_spring.repository.stock;

import com.example.backend_spring.model.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Integer> {

    List<Depot> findByNomContainingIgnoreCase(String nom);

    List<Depot> findByActif(Boolean actif);
}
