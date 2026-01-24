package com.example.backend_spring.repositories;

import com.example.backend_spring.models.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {
    Optional<CommandeClient> findByReference(String reference);
    List<CommandeClient> findByClientIdOrderByDateCommandeDesc(int clientId);
}
