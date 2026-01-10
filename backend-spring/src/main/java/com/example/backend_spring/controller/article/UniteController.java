package com.example.backend_spring.controller.article;

import com.example.backend_spring.service.article.UniteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unites")
@CrossOrigin(origins = "*")
public class UniteController {

    private final UniteService uniteService;

    public UniteController(UniteService uniteService) {
        this.uniteService = uniteService;
    }
}
