package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.LigneReceptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-reception")
@CrossOrigin(origins = "*")
public class LigneReceptionController {

    private final LigneReceptionService ligneReceptionService;

    public LigneReceptionController(LigneReceptionService ligneReceptionService) {
        this.ligneReceptionService = ligneReceptionService;
    }
}
