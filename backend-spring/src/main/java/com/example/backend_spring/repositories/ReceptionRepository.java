package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReceptionRepository extends JpaRepository<Reception, Integer> {
    Optional<Reception> findByReference(String reference);
}
