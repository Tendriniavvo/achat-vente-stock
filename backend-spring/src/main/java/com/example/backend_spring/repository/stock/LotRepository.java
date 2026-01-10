package com.example.backend_spring.repository.stock;

import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {

    Optional<Lot> findByNumeroLot(String numeroLot);

    List<Lot> findByArticle(Article article);

    List<Lot> findByConforme(Boolean conforme);

    @Query("SELECT l FROM Lot l WHERE l.dateExpiration <= :date")
    List<Lot> findLotsExpires(LocalDateTime date);

    @Query("SELECT l FROM Lot l WHERE l.dateExpiration BETWEEN :dateDebut AND :dateFin")
    List<Lot> findLotsAExpirer(LocalDateTime dateDebut, LocalDateTime dateFin);
}
