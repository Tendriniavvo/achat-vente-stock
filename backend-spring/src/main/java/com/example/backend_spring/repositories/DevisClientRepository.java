package com.example.backend_spring.repositories;

import com.example.backend_spring.models.DevisClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DevisClientRepository extends JpaRepository<DevisClient, Integer> {
    Optional<DevisClient> findByReference(String reference);
}
