package com.example.backend_spring.repositories;

import com.example.backend_spring.models.AjustementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AjustementStockRepository extends JpaRepository<AjustementStock, Integer> {
    Optional<AjustementStock> findByReference(String reference);

    List<AjustementStock> findByInventaireId(int inventaireId);

    @Query("select distinct a from AjustementStock a " +
            "left join fetch a.inventaire i " +
            "left join fetch a.article " +
            "left join fetch a.emplacement " +
            "left join fetch a.lot " +
            "left join fetch a.utilisateurProposition " +
            "left join fetch a.utilisateurValidation")
    List<AjustementStock> findAllWithDetails();

    @Query("select distinct a from AjustementStock a " +
            "left join fetch a.inventaire i " +
            "left join fetch a.article " +
            "left join fetch a.emplacement " +
            "left join fetch a.lot " +
            "left join fetch a.utilisateurProposition " +
            "left join fetch a.utilisateurValidation " +
            "where i.id = :inventaireId")
    List<AjustementStock> findByInventaireIdWithDetails(@Param("inventaireId") int inventaireId);
}
