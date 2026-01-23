package com.example.backend_spring.repositories;

import com.example.backend_spring.models.JournalAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Integer> {
}
