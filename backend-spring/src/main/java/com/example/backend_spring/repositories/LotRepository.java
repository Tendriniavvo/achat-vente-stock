package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {
    Optional<Lot> findByNumeroLot(String numeroLot);
    
    List<Lot> findByArticleIdAndQuantiteGreaterThanAndConformeTrueOrderByDateExpirationAsc(int articleId, int minQuantite);
    
    List<Lot> findByArticleIdAndQuantiteGreaterThanAndConformeTrueOrderByDateEntreeAsc(int articleId, int minQuantite);

    @Query("select l from Lot l where l.article.id = :articleId and l.conforme = true and coalesce(l.quantite, 0) > 0")
    List<Lot> findAvailableLotsByArticleId(@Param("articleId") int articleId);
}
