package com.example.backend_spring.services;

import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.JournalAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JournalAuditService {
    private final JournalAuditRepository journalAuditRepository;

    public List<JournalAudit> getAllAudits() {
        return journalAuditRepository.findAll();
    }

    public List<JournalAudit> getAuditsByModuleAndReference(String module, String reference) {
        return journalAuditRepository.findByModuleAndReferenceObjetOrderByDateActionDesc(module, reference);
    }

    public Optional<JournalAudit> getAuditById(int id) {
        return journalAuditRepository.findById(id);
    }

    @Transactional
    public JournalAudit saveAudit(JournalAudit audit) {
        return journalAuditRepository.save(audit);
    }

    @Transactional
    public void logAction(Utilisateur utilisateur, String action, String module, String referenceObjet, String details) {
        JournalAudit audit = new JournalAudit();
        audit.setUtilisateur(utilisateur);
        audit.setAction(action);
        audit.setModule(module);
        audit.setReferenceObjet(referenceObjet);
        audit.setDetails(details);
        audit.setDateAction(LocalDateTime.now());
        journalAuditRepository.save(audit);
    }
}
