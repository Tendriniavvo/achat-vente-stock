package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
    @Query("SELECT l FROM Livraison l " +
            "LEFT JOIN FETCH l.commandeClient " +
            "LEFT JOIN FETCH l.commandeClient.client " +
            "LEFT JOIN FETCH l.depot " +
            "LEFT JOIN FETCH l.utilisateur " +
            "WHERE l.id = :id")
    Optional<Livraison> findById(@Param("id") int id);

    Optional<Livraison> findByReference(String reference);
}
