package com.example.backend_spring.service.audit;

import com.example.backend_spring.repository.audit.JournalAuditRepository;
import org.springframework.stereotype.Service;

@Service
public class JournalAuditService {

    private final JournalAuditRepository journalAuditRepository;

    public JournalAuditService(JournalAuditRepository journalAuditRepository) {
        this.journalAuditRepository = journalAuditRepository;
    }
}
