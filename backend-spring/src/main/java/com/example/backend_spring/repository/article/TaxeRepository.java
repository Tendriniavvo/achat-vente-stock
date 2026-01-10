package com.example.backend_spring.repository.article;

import com.example.backend_spring.model.Taxe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaxeRepository extends JpaRepository<Taxe, Integer> {

    List<Taxe> findByNom(String nom);

    @Query("SELECT t FROM Taxe t WHERE (t.dateDebut IS NULL OR t.dateDebut <= :date) AND (t.dateFin IS NULL OR t.dateFin >= :date)")
    List<Taxe> findTaxesValides(LocalDateTime date);
}
