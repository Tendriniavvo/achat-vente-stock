package com.example.backend_spring.repository.audit;

import com.example.backend_spring.model.JournalAudit;
import com.example.backend_spring.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Integer> {

    List<JournalAudit> findByUtilisateur(Utilisateur utilisateur);

    List<JournalAudit> findByModule(String module);

    List<JournalAudit> findByAction(String action);

    @Query("SELECT j FROM JournalAudit j WHERE j.dateAction BETWEEN :dateDebut AND :dateFin")
    List<JournalAudit> findByDateActionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT j FROM JournalAudit j WHERE j.referenceObjet = :reference")
    List<JournalAudit> findByReferenceObjet(String reference);

    @Query("SELECT j FROM JournalAudit j WHERE j.utilisateur = :utilisateur AND j.dateAction BETWEEN :dateDebut AND :dateFin ORDER BY j.dateAction DESC")
    List<JournalAudit> findHistoriqueUtilisateur(Utilisateur utilisateur, LocalDateTime dateDebut,
            LocalDateTime dateFin);
}
