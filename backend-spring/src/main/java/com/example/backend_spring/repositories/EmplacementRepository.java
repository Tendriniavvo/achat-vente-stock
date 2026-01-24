package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmplacementRepository extends JpaRepository<Emplacement, Integer> {
    List<Emplacement> findByDepotId(int depotId);
    List<Emplacement> findByParentId(Integer parentId);
    List<Emplacement> findByDepotIdAndParentIsNull(int depotId);
}
