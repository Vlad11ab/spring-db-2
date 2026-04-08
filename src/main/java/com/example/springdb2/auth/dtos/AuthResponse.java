package com.example.springdb2.auth.dtos;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;

import java.util.Set;

public record AuthResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<PermissionGroup> permissionGroups,
        Set<UserPermission> directPermissions,
        Set<UserPermission> effectivePermissions,
        String token
) {}
