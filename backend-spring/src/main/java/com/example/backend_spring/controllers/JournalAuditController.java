package com.example.backend_spring.controllers;

import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.services.JournalAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JournalAuditController {

    private final JournalAuditService journalAuditService;

    @GetMapping
    public List<JournalAudit> getAllAudits() {
        return journalAuditService.getAllAudits();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalAudit> getAuditById(@PathVariable int id) {
        return journalAuditService.getAuditById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public JournalAudit saveAudit(@RequestBody JournalAudit audit) {
        return journalAuditService.saveAudit(audit);
    }
}
