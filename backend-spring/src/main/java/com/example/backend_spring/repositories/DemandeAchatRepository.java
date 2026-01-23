package com.example.backend_spring.repositories;

import com.example.backend_spring.models.DemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeAchatRepository extends JpaRepository<DemandeAchat, Integer> {
    Optional<DemandeAchat> findByReference(String reference);

    @Query("SELECT d FROM DemandeAchat d LEFT JOIN FETCH d.lignes LEFT JOIN FETCH d.demandeur u LEFT JOIN FETCH u.departement WHERE d.id = :id")
    Optional<DemandeAchat> findByIdWithDetails(@Param("id") int id);

    @Query("SELECT d FROM DemandeAchat d LEFT JOIN FETCH d.lignes LEFT JOIN FETCH d.demandeur u LEFT JOIN FETCH u.departement ORDER BY d.dateCreation DESC")
    List<DemandeAchat> findAllWithDetails();
}
