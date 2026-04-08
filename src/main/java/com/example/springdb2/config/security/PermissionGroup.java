package com.example.springdb2.config.security;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@AllArgsConstructor
public enum PermissionGroup {
    STUDENT_BASIC(EnumSet.of(
            UserPermission.STUDENT_READ,
            UserPermission.BOOK_READ,
            UserPermission.BOOK_WRITE
    )),
    STUDENT_MANAGER(EnumSet.of(
            UserPermission.STUDENT_READ,
            UserPermission.STUDENT_EDIT
    )),
    BOOK_EDITOR(EnumSet.of(
            UserPermission.BOOK_READ,
            UserPermission.BOOK_EDIT,
            UserPermission.BOOK_WRITE
    )),
    PERMISSION_ADMIN(EnumSet.of(
            UserPermission.STUDENT_PERMISSIONS_EDIT
    ));

    private final Set<UserPermission> permissions;

    public Set<UserPermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}
