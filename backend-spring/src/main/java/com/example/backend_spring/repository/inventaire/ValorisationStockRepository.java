package com.example.backend_spring.repository.inventaire;

import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.ValorisationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValorisationStockRepository extends JpaRepository<ValorisationStock, Integer> {

    List<ValorisationStock> findByDepot(Depot depot);

    Optional<ValorisationStock> findTopByOrderByDateValorisationDesc();

    @Query("SELECT v FROM ValorisationStock v WHERE v.dateValorisation BETWEEN :dateDebut AND :dateFin")
    List<ValorisationStock> findByDateValorisationBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT v FROM ValorisationStock v WHERE v.depot = :depot ORDER BY v.dateValorisation DESC")
    List<ValorisationStock> findHistoriqueByDepot(Depot depot);
}
