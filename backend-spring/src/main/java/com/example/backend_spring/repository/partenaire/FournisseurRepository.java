package com.example.backend_spring.repository.partenaire;

import com.example.backend_spring.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {

    List<Fournisseur> findByNomContainingIgnoreCase(String nom);

    List<Fournisseur> findByActif(Boolean actif);

    List<Fournisseur> findByEmail(String email);
}
