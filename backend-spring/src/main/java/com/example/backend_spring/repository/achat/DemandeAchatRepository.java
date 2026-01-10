package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.DemandeAchat;
import com.example.backend_spring.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeAchatRepository extends JpaRepository<DemandeAchat, Integer> {

    Optional<DemandeAchat> findByReference(String reference);

    List<DemandeAchat> findByDemandeur(Utilisateur demandeur);

    List<DemandeAchat> findByStatut(String statut);

    @Query("SELECT d FROM DemandeAchat d WHERE d.dateCreation BETWEEN :dateDebut AND :dateFin")
    List<DemandeAchat> findByDateCreationBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT d FROM DemandeAchat d WHERE d.statut = 'soumise' ORDER BY d.dateCreation ASC")
    List<DemandeAchat> findDemandesEnAttente();
}
