package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Taxe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxeRepository extends JpaRepository<Taxe, Integer> {
}
