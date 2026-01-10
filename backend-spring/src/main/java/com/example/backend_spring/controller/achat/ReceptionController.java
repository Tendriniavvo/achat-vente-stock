package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.ReceptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receptions")
@CrossOrigin(origins = "*")
public class ReceptionController {

    private final ReceptionService receptionService;

    public ReceptionController(ReceptionService receptionService) {
        this.receptionService = receptionService;
    }
}
