package com.example.springdb2.student.dtos;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;

import java.util.Set;

public record StudentCreateResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Integer age,
        Integer nrCrediteNecesare,
        Integer nrCrediteEfectuate,
        Set<PermissionGroup> permissionGroups,
        Set<UserPermission> directPermissions,
        Set<UserPermission> effectivePermissions,
        String token
        ){}
