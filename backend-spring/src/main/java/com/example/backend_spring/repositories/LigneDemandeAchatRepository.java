package com.example.backend_spring.repositories;

import com.example.backend_spring.models.DemandeAchat;
import com.example.backend_spring.models.LigneDemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneDemandeAchatRepository extends JpaRepository<LigneDemandeAchat, Integer> {
    List<LigneDemandeAchat> findByDemandeAchat(DemandeAchat demandeAchat);

    List<LigneDemandeAchat> findByDemandeAchatId(int demandeAchatId);
}
