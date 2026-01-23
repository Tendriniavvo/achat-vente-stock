package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventaireRepository extends JpaRepository<Inventaire, Integer> {
    Optional<Inventaire> findByReference(String reference);
}
