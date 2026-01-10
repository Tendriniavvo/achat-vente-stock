package com.example.backend_spring.controller.security;

import com.example.backend_spring.service.security.PermissionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
