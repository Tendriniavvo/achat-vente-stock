package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.departement WHERE u.email = :email")
    Optional<Utilisateur> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.departement WHERE u.id = :id")
    Optional<Utilisateur> findById(@Param("id") int id);
}
