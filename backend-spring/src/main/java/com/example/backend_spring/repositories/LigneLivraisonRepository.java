package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface LigneLivraisonRepository extends JpaRepository<LigneLivraison, Integer> {
    @Query("SELECT ll FROM LigneLivraison ll " +
            "LEFT JOIN FETCH ll.article " +
            "LEFT JOIN FETCH ll.emplacement " +
            "LEFT JOIN FETCH ll.emplacement.depot " +
            "LEFT JOIN FETCH ll.lot " +
            "WHERE ll.livraison.id = :livraisonId")
    List<LigneLivraison> findByLivraisonId(@Param("livraisonId") int livraisonId);
}
