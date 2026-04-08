package com.example.springdb2.student.dtos;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record StudentPermissionsUpdateRequest(
        @NotEmpty
        Set<@NotNull PermissionGroup> permissionGroups,
        Set<@NotNull UserPermission> directPermissions
) {}
