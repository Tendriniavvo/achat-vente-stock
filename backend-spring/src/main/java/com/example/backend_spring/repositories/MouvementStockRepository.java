package com.example.backend_spring.repositories;

import com.example.backend_spring.models.MouvementStock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {
    Optional<MouvementStock> findByReference(String reference);

    @Query("select distinct m from MouvementStock m " +
            "left join fetch m.lignes l " +
            "left join fetch l.article " +
            "left join fetch l.lot " +
            "left join fetch l.emplacement " +
            "left join fetch m.depot " +
            "left join fetch m.emplacement " +
            "left join fetch m.depotDestination " +
            "left join fetch m.emplacementDestination " +
            "where m.id = :id")
    Optional<MouvementStock> findByIdWithDetails(@Param("id") int id);
}
