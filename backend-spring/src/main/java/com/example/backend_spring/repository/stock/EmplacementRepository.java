package com.example.backend_spring.repository.stock;

import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmplacementRepository extends JpaRepository<Emplacement, Integer> {

    List<Emplacement> findByDepot(Depot depot);

    Optional<Emplacement> findByDepotAndCode(Depot depot, String code);
}
