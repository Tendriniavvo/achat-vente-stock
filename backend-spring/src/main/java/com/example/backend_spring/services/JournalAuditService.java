package com.example.backend_spring.services;

import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.repositories.JournalAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JournalAuditService {
    private final JournalAuditRepository journalAuditRepository;

    public List<JournalAudit> getAllAudits() {
        return journalAuditRepository.findAll();
    }

    public Optional<JournalAudit> getAuditById(int id) {
        return journalAuditRepository.findById(id);
    }

    @Transactional
    public JournalAudit saveAudit(JournalAudit audit) {
        return journalAuditRepository.save(audit);
    }
}
