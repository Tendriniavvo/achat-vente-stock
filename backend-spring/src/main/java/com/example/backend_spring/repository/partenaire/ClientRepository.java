package com.example.backend_spring.repository.partenaire;

import com.example.backend_spring.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    List<Client> findByNomContainingIgnoreCase(String nom);

    List<Client> findByActif(Boolean actif);

    List<Client> findByEmail(String email);
}
