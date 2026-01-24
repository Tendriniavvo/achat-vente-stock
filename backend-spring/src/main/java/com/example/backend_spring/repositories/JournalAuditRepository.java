package com.example.backend_spring.repositories;

import com.example.backend_spring.models.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Integer> {
    List<JournalAudit> findByModuleAndReferenceObjetOrderByDateActionDesc(String module, String referenceObjet);
}
