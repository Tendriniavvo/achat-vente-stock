package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Permission;
import com.example.backend_spring.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/role/{roleId}")
    public List<Permission> getPermissionsByRole(@PathVariable int roleId) {
        return permissionService.getPermissionsByRole(roleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable int id) {
        return permissionService.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Permission savePermission(@RequestBody Permission permission) {
        return permissionService.savePermission(permission);
    }

    @PostMapping("/role/{roleId}/bulk")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable int roleId, @RequestBody List<Permission> permissions) {
        permissionService.updateRolePermissions(roleId, permissions);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable int id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
