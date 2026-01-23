package com.example.backend_spring.services;

import com.example.backend_spring.models.Permission;
import com.example.backend_spring.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(int id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(int id) {
        permissionRepository.deleteById(id);
    }
}
