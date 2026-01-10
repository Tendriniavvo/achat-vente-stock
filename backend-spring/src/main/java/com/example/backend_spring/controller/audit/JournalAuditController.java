package com.example.backend_spring.controller.audit;

import com.example.backend_spring.service.audit.JournalAuditService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/journal-audit")
@CrossOrigin(origins = "*")
public class JournalAuditController {

    private final JournalAuditService journalAuditService;

    public JournalAuditController(JournalAuditService journalAuditService) {
        this.journalAuditService = journalAuditService;
    }
}
