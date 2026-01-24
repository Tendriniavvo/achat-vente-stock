package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByRoleId(int roleId);
    void deleteByRoleId(int roleId);
}
