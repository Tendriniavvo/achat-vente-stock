package com.example.backend_spring.controller.article;

import com.example.backend_spring.service.article.TaxeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taxes")
@CrossOrigin(origins = "*")
public class TaxeController {

    private final TaxeService taxeService;

    public TaxeController(TaxeService taxeService) {
        this.taxeService = taxeService;
    }
}
