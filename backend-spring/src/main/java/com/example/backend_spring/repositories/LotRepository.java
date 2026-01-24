package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {
    Optional<Lot> findByNumeroLot(String numeroLot);
}
