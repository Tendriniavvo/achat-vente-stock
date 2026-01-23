package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneDemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneDemandeAchatRepository extends JpaRepository<LigneDemandeAchat, Integer> {
}
