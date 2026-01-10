package com.example.backend_spring.controller.security;

import com.example.backend_spring.dto.RoleDto;
import com.example.backend_spring.model.Role;
import com.example.backend_spring.service.security.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getNom(), role.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }
}
