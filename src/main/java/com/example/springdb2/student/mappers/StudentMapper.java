package com.example.springdb2.student.mappers;

import com.example.springdb2.book.mappers.BookMapper;
import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.model.Student;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Component
public class StudentMapper {
    private final PasswordEncoder passwordEncoder;

    public StudentMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Student toEntity(StudentCreateRequest req){
        if(req == null) return null;

        return Student.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .age(req.age())
                .password(passwordEncoder.encode(req.password()))
                .permissionGroups(EnumSet.of(PermissionGroup.STUDENT_BASIC))
                .permissions(Collections.emptySet())
                .nrCrediteNecesare(req.nrCrediteNecesare())
                .nrCrediteEfectuate(req.nrCrediteEfectuate())
                .build();
    }

    public StudentResponse toDto(Student student){
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getAge(),
                student.getNrCrediteNecesare(),
                student.getNrCrediteEfectuate(),
                copyGroups(student.getPermissionGroups()),
                copyPermissions(student.getPermissions()),
                copyPermissions(student.getEffectivePermissions()),
                student.getBooks().stream().map(BookMapper::toDto).toList()
        );
    }

    public StudentCreateResponse studentToCreateResponse(Student student, String token){
        return new StudentCreateResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getAge(),
                student.getNrCrediteNecesare(),
                student.getNrCrediteEfectuate(),
                copyGroups(student.getPermissionGroups()),
                copyPermissions(student.getPermissions()),
                copyPermissions(student.getEffectivePermissions()),
                token
        );
    }

    private Set<PermissionGroup> copyGroups(Set<PermissionGroup> groups) {
        if (groups == null || groups.isEmpty()) {
            return Collections.emptySet();
        }
        return EnumSet.copyOf(groups);
    }

    private Set<UserPermission> copyPermissions(Set<UserPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptySet();
        }
        return EnumSet.copyOf(permissions);
    }

}
