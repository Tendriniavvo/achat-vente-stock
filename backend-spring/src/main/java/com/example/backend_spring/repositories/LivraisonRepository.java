package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    Optional<Livraison> findByReference(String reference);
}
