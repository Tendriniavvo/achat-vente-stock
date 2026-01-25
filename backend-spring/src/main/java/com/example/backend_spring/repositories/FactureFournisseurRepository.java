package com.example.backend_spring.repositories;

import com.example.backend_spring.models.FactureFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FactureFournisseurRepository extends JpaRepository<FactureFournisseur, Integer> {
    Optional<FactureFournisseur> findByReference(String reference);

    Optional<FactureFournisseur> findByReceptionId(int receptionId);
}
