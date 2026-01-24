package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneReception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneReceptionRepository extends JpaRepository<LigneReception, Integer> {
}
