package com.example.backend_spring.repositories;

import com.example.backend_spring.models.DemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DemandeAchatRepository extends JpaRepository<DemandeAchat, Integer> {
    Optional<DemandeAchat> findByReference(String reference);
}
