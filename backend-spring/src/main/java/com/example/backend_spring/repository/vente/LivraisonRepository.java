package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.CommandeClient;
import com.example.backend_spring.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {

    Optional<Livraison> findByReference(String reference);

    List<Livraison> findByCommandeClient(CommandeClient commandeClient);

    List<Livraison> findByStatut(String statut);

    @Query("SELECT l FROM Livraison l WHERE l.dateLivraison BETWEEN :dateDebut AND :dateFin")
    List<Livraison> findByDateLivraisonBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
}
