package com.example.springdb2.student.service;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPermissionsUpdateRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.exceptions.EmptyStudentUpdateRequestException;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.mappers.StudentMapper;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.command.impl.StudentCommandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentCommandServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private JWTTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;
    private StudentMapper studentMapper;
    private StudentCommandServiceImpl studentCommandServiceImpl;

    @BeforeEach
    void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        studentMapper = new StudentMapper(passwordEncoder);
        studentCommandServiceImpl = new StudentCommandServiceImpl(studentRepository, jwtTokenProvider, passwordEncoder, studentMapper);
    }

    @Test
    void createStudentWithBooksThrowsStudentAlreadyExistsException() {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50);
        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(true);

        assertThrows(StudentAlreadyExistsException.class, () -> studentCommandServiceImpl.createStudentWithBooks(request));
    }

    @Test
    void createStudentWithBooksReturnsAndPersists() {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50);
        Student saved = Student.builder()
                .id(1L)
                .books(Collections.emptyList())
                .firstName("Vlad")
                .lastName("Breazu")
                .email("breazuvlad11@gmail.com")
                .age(22)
                .password("{bcrypt}encoded")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(50)
                .build();

        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        StudentResponse actual = studentCommandServiceImpl.createStudentWithBooks(request);

        assertEquals(1L, actual.id());
        assertEquals("Vlad", actual.firstName());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudentThrowsStudentAlreadyExistsException() {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50);
        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(true);

        assertThrows(StudentAlreadyExistsException.class, () -> studentCommandServiceImpl.createStudent(request));
    }

    @Test
    void createStudentReturnsAndPersists() {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50);
        Student saved = Student.builder()
                .id(1L)
                .books(Collections.emptyList())
                .firstName("Vlad")
                .lastName("Breazu")
                .email("breazuvlad11@gmail.com")
                .age(22)
                .password("{bcrypt}encoded")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(50)
                .build();

        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(saved);
        when(jwtTokenProvider.generateToken(saved)).thenReturn("jwt-token");

        StudentCreateResponse actual = studentCommandServiceImpl.createStudent(request);

        assertEquals(1L, actual.id());
        assertEquals("jwt-token", actual.token());
    }

    @Test
    void patchThrowsWhenNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentCommandServiceImpl.patchStudent(1L, new StudentPatchRequest("vlad@gmail.com", "parola", 22)));
    }

    @Test
    void patchUpdatesOnlyProvidedFields() {
        Student existing = Student.builder()
                .id(1L)
                .books(Collections.emptyList())
                .firstName("Vlad")
                .lastName("Breazu")
                .email("breazuvlad11@gmail.com")
                .age(22)
                .password("{bcrypt}old")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(50)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentResponse response = studentCommandServiceImpl.patchStudent(1L, new StudentPatchRequest(null, null, 22));

        assertEquals("breazuvlad11@gmail.com", response.email());
        assertEquals(22, response.nrCrediteEfectuate());
    }

    @Test
    void patchEncodesPasswordWhenProvided() {
        Student existing = Student.builder()
                .id(1L)
                .books(Collections.emptyList())
                .firstName("Vlad")
                .lastName("Breazu")
                .email("breazuvlad11@gmail.com")
                .age(22)
                .password("{bcrypt}old")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(50)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentResponse response = studentCommandServiceImpl.patchStudent(1L, new StudentPatchRequest("nou@gmail.com", "parola_noua", 99));

        assertEquals("nou@gmail.com", response.email());
        assertEquals(99, response.nrCrediteEfectuate());
        assertNotEquals("parola_noua", existing.getPassword());
        assertNotNull(existing.getPassword());
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentCommandServiceImpl.updateStudent(1L, new StudentPutRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50)));
    }

    @Test
    void updateThrowsWhenAlreadyExists() {
        Student existing = Student.builder().id(1L).email("old@gmail.com").build();
        StudentPutRequest request = new StudentPutRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 50);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(true);

        assertThrows(StudentAlreadyExistsException.class, () -> studentCommandServiceImpl.updateStudent(1L, request));
    }

    @Test
    void updateThrowsWhenEmptyPayload() {
        assertThrows(EmptyStudentUpdateRequestException.class, () -> studentCommandServiceImpl.updateStudent(1L, new StudentPutRequest(null, null, null, null, null, null, null)));
    }

    @Test
    void updateReplacesAllFieldsAndEncodesPassword() {
        Student existing = Student.builder()
                .id(1L)
                .books(Collections.emptyList())
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("oldemail@gmail.com")
                .age(22)
                .password("{bcrypt}old")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(65)
                .build();

        StudentPutRequest request = new StudentPutRequest("Vlad", "Breazu", "breazuvlad11@gmail.com", 22, "parola", 100, 80);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailJpql(request.email())).thenReturn(false);
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentResponse actual = studentCommandServiceImpl.updateStudent(1L, request);

        assertEquals("Vlad", actual.firstName());
        assertEquals("breazuvlad11@gmail.com", actual.email());
        assertEquals(80, actual.nrCrediteEfectuate());
        assertNotEquals("parola", existing.getPassword());
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentCommandServiceImpl.deleteStudent(1L));
    }

    @Test
    void deleteRemovesWhenExisting() {
        Student existing = Student.builder().id(1L).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));

        StudentResponse response = studentCommandServiceImpl.deleteStudent(1L);

        assertNotNull(response);
        verify(studentRepository).delete(existing);
    }

    @Test
    void updatePermissionsReplacesPermissionSet() {
        Student existing = Student.builder()
                .id(1L)
                .permissionGroups(EnumSet.of(PermissionGroup.STUDENT_BASIC))
                .permissions(EnumSet.of(UserPermission.STUDENT_READ))
                .build();
        StudentPermissionsUpdateRequest request = new StudentPermissionsUpdateRequest(
                EnumSet.of(PermissionGroup.BOOK_EDITOR, PermissionGroup.PERMISSION_ADMIN),
                EnumSet.of(UserPermission.STUDENT_EDIT)
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentResponse actual = studentCommandServiceImpl.updatePermissions(1L, request);

        assertEquals(EnumSet.of(PermissionGroup.BOOK_EDITOR, PermissionGroup.PERMISSION_ADMIN), actual.permissionGroups());
        assertEquals(EnumSet.of(UserPermission.STUDENT_EDIT), actual.directPermissions());
        assertEquals(EnumSet.of(PermissionGroup.BOOK_EDITOR, PermissionGroup.PERMISSION_ADMIN), existing.getPermissionGroups());
        assertEquals(EnumSet.of(UserPermission.STUDENT_EDIT), existing.getPermissions());
    }
}
