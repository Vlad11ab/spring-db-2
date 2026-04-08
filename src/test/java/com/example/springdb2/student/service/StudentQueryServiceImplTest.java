package com.example.springdb2.student.service;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.mappers.StudentMapper;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.query.StudentQueryService;
import com.example.springdb2.student.service.query.impl.StudentQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentQueryServiceImplTest {

    @Mock
    StudentRepository studentRepository;
    StudentQueryService studentQueryService;

    @BeforeEach()
    void setUp(){
        studentQueryService = new StudentQueryServiceImpl(studentRepository, new StudentMapper(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    @Test
    void getAllStudents(){
        List<Student> students = List.of(
                Student.builder().id(1L)
                        .firstName("Andrei")
                        .lastName("Popescu")
                        .email("andrei.popescu@example.com")
                        .age(20)
                        .password("parolaSigura123")
                        .nrCrediteNecesare(60)
                        .nrCrediteEfectuate(30)
                        .build(),

                Student.builder().id(2L)
                        .firstName("Maria")
                        .lastName("Ionescu")
                        .email("maria.ionescu@facultate.ro")
                        .age(22)
                        .password("studentEminent22")
                        .nrCrediteNecesare(180)
                        .nrCrediteEfectuate(180)
                        .build(),

                Student.builder().id(3L)
                        .firstName("Alexandru")
                        .lastName("Radu")
                        .email("alexandru.radu@test.com")
                        .age(19)
                        .password("qwertyuiop")
                        .nrCrediteNecesare(60)
                        .nrCrediteEfectuate(0)
                        .build(),

                Student.builder().id(4L)
                        .firstName("Elena")
                        .lastName("Dumitrescu")
                        .email("elena.d@campus.ro")
                        .age(21)
                        .password("secretdent")
                        .nrCrediteNecesare(120)
                        .nrCrediteEfectuate(90)
                        .build(),

                Student.builder().id(5L)
                        .firstName("Cristian")
                        .lastName("Vasile")
                        .email("cristi.vasile@mail.com")
                        .age(24)
                        .password("masterat2024")
                        .nrCrediteNecesare(120)
                        .nrCrediteEfectuate(120)
                        .build()
        );List<StudentResponse> expectedList = List.of(
                new StudentResponse(
                        1L,
                        "Andrei",
                        "Popescu",
                        "andrei.popescu@example.com",
                        20,
                        60,
                        30,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptyList()
                ),
                new StudentResponse(
                        2L,
                        "Maria",
                        "Ionescu",
                        "maria.ionescu@facultate.ro",
                        22,
                        180,
                        180,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptyList()
                ),
                new StudentResponse(
                        3L,
                        "Alexandru",
                        "Radu",
                        "alexandru.radu@test.com",
                        19,
                        60,
                        0,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptyList()
                ),
                new StudentResponse(
                        4L,
                        "Elena",
                        "Dumitrescu",
                        "elena.d@campus.ro",
                        21,
                        120,
                        90,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptyList()
                ),
                new StudentResponse(
                        5L,
                        "Cristian",
                        "Vasile",
                        "cristi.vasile@mail.com",
                        24,
                        120,
                        120,
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptySet(),
                        Collections.emptyList()
                )
        );
        when(studentRepository.findAll()).thenReturn(students);
        List<StudentResponse> actualList = studentQueryService.getAllStudents();
        assertEquals(expectedList,actualList);

    }

    @Test
    void getStudentByIdThrowsNotFound(){
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,()->studentQueryService.getStudentById(1L));
    }

    @Test
    void getStudentById(){
        Student student = Student.builder().id(1L)
                .firstName("Andrei")
                .lastName("Popescu")
                .email("andrei.popescu@example.com")
                .age(20)
                .password("parolaSigura123")
                .nrCrediteNecesare(60)
                .nrCrediteEfectuate(30)
                .build();
        StudentResponse expected = new StudentResponse(
                1L,
                "Andrei",
                "Popescu",
                "andrei.popescu@example.com",
                20,
                60,
                30,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptyList());

        when(studentRepository.findById(1L)).thenReturn(Optional.ofNullable(student));
        StudentResponse actual = studentQueryService.getStudentById(1L);
        assertEquals(expected,actual);

    }


}
