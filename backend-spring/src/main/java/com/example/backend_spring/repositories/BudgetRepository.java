package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Budget;
import com.example.backend_spring.models.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByDepartementAndAnnee(Departement departement, int annee);
}
