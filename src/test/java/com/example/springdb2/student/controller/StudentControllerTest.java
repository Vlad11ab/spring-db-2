package com.example.springdb2.student.controller;

import com.example.springdb2.student.dtos.*;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentQueryService studentQueryService;
    @MockitoBean
    private StudentCommandService studentCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    Student student1 = Student.builder().id(1L).firstName("Andrei").lastName("Popescu").email("andrei.popescu@example.com").age(20).password("parolaSigura123").nrCrediteNecesare(60).nrCrediteEfectuate(30).build();
    Student student2 = Student.builder().id(2L).firstName("Maria").lastName("Ionescu").email("maria.ionescu@facultate.ro").age(22).password("studentEminent22").nrCrediteNecesare(180).nrCrediteEfectuate(180).build();
    Student student3 = Student.builder().id(3L).firstName("Alexandru").lastName("Radu").email("alexandru.radu@test.com").age(19).password("qwertyuiop").nrCrediteNecesare(60).nrCrediteEfectuate(0).build();
    Student student4 = Student.builder().id(4L).firstName("Elena").lastName("Dumitrescu").email("elena.d@campus.ro").age(21).password("secretdent").nrCrediteNecesare(120).nrCrediteEfectuate(90).build();
    Student student5 = Student.builder().id(5L).firstName("Cristian").lastName("Vasile").email("cristi.vasile@mail.com").age(24).password("masterat2024").nrCrediteNecesare(120).nrCrediteEfectuate(120).build();

    StudentResponse response1 = new StudentResponse(1L, "Andrei", "Popescu", "andrei.popescu@example.com", "parolaSigura123", 20, 60, 30, Collections.emptyList());
    StudentResponse response2 = new StudentResponse(2L, "Maria", "Ionescu", "maria.ionescu@facultate.ro", "studentEminent22", 22, 180, 180, Collections.emptyList());
    StudentResponse response3 = new StudentResponse(3L, "Alexandru", "Radu", "alexandru.radu@test.com", "qwertyuiop", 19, 60, 0, Collections.emptyList());
    StudentResponse response4 = new StudentResponse(4L, "Elena", "Dumitrescu", "elena.d@campus.ro", "secretdent", 21, 120, 90, Collections.emptyList());
    StudentResponse response5 = new StudentResponse(5L, "Cristian", "Vasile", "cristi.vasile@mail.com", "masterat2024", 24, 120, 120, Collections.emptyList());

    @Test
    void getAllReturnsList() throws Exception {
        when(studentQueryService.getAllStudents()).thenReturn(List.of(response1,response2,response3,response4,response5));

        MvcResult result = mockMvc.perform(get("/api/v1/books-students/students/students-with-books"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void patchReturnsOk() throws Exception {
        StudentPatchRequest patch = new StudentPatchRequest("email@gmail.com","parola",100);
        StudentResponse studentResponse = new StudentResponse(1L,"Vlad","Breazu","email@gmail.com","parola",22,100,50,Collections.emptyList());

        when(studentCommandService.patchStudent(1L,patch)).thenReturn(studentResponse);

        mockMvc.perform(patch("/api/v1/books-students/students/edit/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                        .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Vlad"))
                .andExpect(jsonPath("$.lastName").value("Breazu"))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.password").value("parola"))
                .andExpect(jsonPath("$.age").value(22))
                .andExpect(jsonPath("$.nrCrediteNecesare").value(100))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(50));

                verify(studentCommandService).patchStudent(1L,patch);
    }

    @Test
    void deleteReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/books-students/students/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserReturnsConflictWhenServiceThrowsNotFound() throws Exception {
        doThrow(new StudentNotFoundException(1L)).when(studentCommandService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/books-students/students/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT FOUND"))
                .andExpect(jsonPath("$.message").value("STUDENT_NOT_FOUND_EXCEPTION"));
    }


    @Test
    void updateReturnsUpdatedResponse() throws Exception {
        StudentPutRequest request = new StudentPutRequest("Vlad","Breazu","email@gmail.com",22,"parola",100,50);
        StudentResponse studentResponse = new StudentResponse(1L,"Andrei","Andrei","andrei@gmail.com","parola",21,100,50,Collections.emptyList());

        when(studentCommandService.updateStudent(1L,request)).thenReturn(studentResponse);

        mockMvc.perform(put("/api/v1/books-students/students/edit/update/1")
                .contentType(String.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Andrei"))
                .andExpect(jsonPath("$.lastName").value("Andrei"))
                .andExpect(jsonPath("$.email").value("andrei@gmail.com"))
                .andExpect(jsonPath("$.password").value("parola"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.nrCrediteNecesare").value(100))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(50));

        verify(studentCommandService).updateStudent(1L,request);

    }

    @Test
    void createCarReturnsConflictWhenServiceThrowsAlreadyExists() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad","Breazu","email@gmail.com",22,"parola",100,50);
        StudentResponse studentResponse = new StudentResponse(1L,"Andrei","Andrei","andrei@gmail.com","parola",21,100,50,Collections.emptyList());

        when(studentCommandService.createStudent(request)).thenThrow(new StudentAlreadyExistsException());
        mockMvc.perform(post("/api/v1/books-students/students/add")
                        .contentType(String.valueOf(org.junit.jupiter.api.MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("STUDENT_ALREADY_EXISTS_EXCEPTION"));
    }

    @Test
    void createReturns201() throws Exception{
        StudentCreateRequest request = new StudentCreateRequest("Vlad","Breazu","email@gmail.com",22,"parola",100,50);
        StudentCreateResponse studentResponse = new StudentCreateResponse(1L,"Andrei","Andrei","andrei@gmail.com","parola",21,50,100);

        when(studentCommandService.createStudent(request)).thenReturn(studentResponse);

        mockMvc.perform(post("/api/v1/books-students/students/add")
                        .contentType(String.valueOf(org.junit.jupiter.api.MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Andrei"))
                .andExpect(jsonPath("$.lastName").value("Andrei"))
                .andExpect(jsonPath("$.email").value("andrei@gmail.com"))
                .andExpect(jsonPath("$.password").value("parola"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.nrCrediteNecesare").value(100))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(50));

        verify(studentCommandService).createStudent(request);

    }


}
