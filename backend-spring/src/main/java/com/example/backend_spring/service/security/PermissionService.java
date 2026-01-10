package com.example.backend_spring.service.security;

import com.example.backend_spring.repository.security.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
}
