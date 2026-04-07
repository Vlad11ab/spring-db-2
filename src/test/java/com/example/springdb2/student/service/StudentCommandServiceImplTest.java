package com.example.springdb2.student.service;

import com.example.springdb2.student.dtos.*;
import com.example.springdb2.student.exceptions.EmptyStudentUpdateRequestException;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.command.impl.StudentCommandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentCommandServiceImplTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentCommandServiceImpl studentCommandServiceImpl;

    @BeforeEach
    void setUp(){
        studentCommandServiceImpl = new StudentCommandServiceImpl(studentRepository);
    }


    @Test
    void createStudentWithBooksThrowsStudentAlreadyExistsException(){
        StudentCreateRequest toCreate = new StudentCreateRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);

        when(studentRepository.existsByEmailJQPL(toCreate.email())).thenReturn(true);
        assertThrows(StudentAlreadyExistsException.class,()->studentCommandServiceImpl.createStudentWithBooks(toCreate));
    }

    @Test
    void createStudentWithBooksReturnsAndPersists(){
        StudentCreateRequest toCreate = new StudentCreateRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);
        Student toSave = Student.builder().firstName("Vlad").lastName("Breazu").email("breazuvlad11@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        Student saved = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").email("breazuvlad11@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        StudentResponse expected = new StudentResponse(1L,"Vlad","Breazu","breazuvlad11@gmail.com","parola",22,100,50, Collections.emptyList());

        when(studentRepository.existsByEmailJQPL(toCreate.email())).thenReturn(false);
        when(studentRepository.save(toSave)).thenReturn(saved);

        StudentResponse actual = studentCommandServiceImpl.createStudentWithBooks(toCreate);

        assertEquals(expected,actual);


    }

    @Test
    void createStudentThrowsStudentAlreadyExistsException(){
        StudentCreateRequest toCreate = new StudentCreateRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);

        when(studentRepository.existsByEmailJQPL(toCreate.email())).thenReturn(true);
        assertThrows(StudentAlreadyExistsException.class,()->studentCommandServiceImpl.createStudent(toCreate));
    }

    @Test
    void createStudentReturnsAndPersists(){
        StudentCreateRequest toCreate = new StudentCreateRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);
        Student toSave = Student.builder().firstName("Vlad").lastName("Breazu").email("breazuvlad11@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        Student saved = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").email("breazuvlad11@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        StudentCreateResponse expected = new StudentCreateResponse(1L,"Vlad","Breazu","breazuvlad11@gmail.com","parola",22,100,50);

        when(studentRepository.existsByEmailJQPL(toCreate.email())).thenReturn(false);
        when(studentRepository.save(toSave)).thenReturn(saved);

        StudentCreateResponse actual = studentCommandServiceImpl.createStudent(toCreate);

        assertEquals(expected,actual);
    }

    @Test
    void patchThrowsWhenNotFound(){
        Student existing = Student.builder().build();
        StudentPatchRequest patch = new StudentPatchRequest("Vlad","Breazu",22);

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class,()->studentCommandServiceImpl.patchStudent(1L,patch));
    }

    @Test
    void patchUpdatesOnlyProvidedFields(){
         Student existing = Student.builder()
                 .id(1L)
                 .firstName("Vlad")
                 .lastName("Breazu")
                 .email("breazuvlad11@gmail.com")
                 .age(22).password("parola")
                 .nrCrediteNecesare(100)
                 .nrCrediteEfectuate(50)
                 .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentPatchRequest patch = new StudentPatchRequest(null,null,22);
        StudentResponse response = studentCommandServiceImpl.patchStudent(1L,patch);

        assertEquals(existing.getEmail(),response.email());
        assertEquals(existing.getPassword(),response.password());
        assertEquals(existing.getNrCrediteEfectuate(),response.nrCrediteEfectuate());
        verify(studentRepository).save(existing);
    }

    @Test
    void patchUpdatesAllOptionalFields(){
        Student existing = Student.builder()
                .id(1L)
                .firstName("Vlad")
                .lastName("Breazu")
                .email("breazuvlad11@gmail.com")
                .age(22).password("parola")
                .nrCrediteNecesare(100)
                .nrCrediteEfectuate(50)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentPatchRequest patchRequest = new StudentPatchRequest("nou@gmail.com","parola_noua",99);
        StudentResponse response = studentCommandServiceImpl.patchStudent(1L,patchRequest);

        assertEquals("nou@gmail.com",response.email());
        assertEquals("parola_noua",response.password());
        assertEquals(99,response.nrCrediteEfectuate());
        verify(studentRepository).save(existing);
    }

    @Test
    void updateThrowsWhenNotFound(){
        StudentPutRequest putRequest = new StudentPutRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,()->studentCommandServiceImpl.updateStudent(1L,putRequest));
    }

    @Test
    void updateThrowsWhenAlreadyExists(){
        StudentPutRequest request = new StudentPutRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,50);
        Student existing = new Student();
        existing.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailJQPL("breazuvlad11@gmail.com")).thenReturn(true);
        assertThrows(StudentAlreadyExistsException.class,()->studentCommandServiceImpl.updateStudent(1L,request));
    }

    @Test
    void updateThrowsWhenEmptyPayload(){
        Student existing = new Student();
        existing.setId(1L);

        StudentPutRequest emptyUpdate = new StudentPutRequest(null,null,null,null,null,null,null);
        assertThrows(EmptyStudentUpdateRequestException.class,()->studentCommandServiceImpl.updateStudent(1L,emptyUpdate));
    }

    @Test
    void updateReplacesAllFields(){
        StudentPutRequest request = new StudentPutRequest("Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,80);
        Student existing = Student.builder().id(1L).books(Collections.emptyList()).firstName("OldFirstName").lastName("OldLastName").email("oldemail@gmail.com").age(22).password("OldPassword").nrCrediteNecesare(100).nrCrediteEfectuate(65).build();
        Student toSave = new Student(1L,Collections.emptyList(),"Vlad","Breazu","breazuvlad11@gmail.com",22,"parola",100,80);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailJQPL(request.email())).thenReturn(false);
        when(studentRepository.save(toSave)).thenReturn(toSave);

        StudentResponse expected = new StudentResponse(1L,"Vlad","Breazu","breazuvlad11@gmail.com","parola",22,100,80,Collections.emptyList());
        StudentResponse actual = studentCommandServiceImpl.updateStudent(1L,request);

        assertEquals(expected,actual);


    }

    @Test
    void deleteThrowsWhenMissing(){
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,()->studentCommandServiceImpl.deleteStudent(1L));
    }

    @Test
    void deleteRemovesWhenExisting(){
        Student existing = new Student();
        existing.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        studentCommandServiceImpl.deleteStudent(1L);
        verify(studentRepository).delete(existing);
    }
}

