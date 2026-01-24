package com.example.backend_spring.repositories;

import com.example.backend_spring.models.FactureClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureClientRepository extends JpaRepository<FactureClient, Integer> {
    Optional<FactureClient> findByReference(String reference);
    List<FactureClient> findByClientIdOrderByDateFactureDesc(int clientId);
}
