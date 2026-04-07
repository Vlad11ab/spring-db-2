package com.example.springdb2.student.integration;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanDatabase(){
        studentRepository.deleteAll();
    }


    @Test
    void createGetUpdatePatchDeleteFlow() throws Exception{
        StudentCreateRequest createRequest = new StudentCreateRequest(
                "Vlad",
                "Breazu",
                "vlad@gmail.com",
                22,
                "parola",
                50,
                100
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/books-students/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        StudentResponse created = objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), StudentResponse.class);

        mockMvc.perform(get("/api/v1/books-students/students/get/{studentId}", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Vlad"))
                .andExpect(jsonPath("$.lastName").value("Breazu"))
                .andExpect(jsonPath("$.email").value("vlad@gmail.com"))
                .andExpect(jsonPath("$.age").value(22))
                .andExpect(jsonPath("$.password").value("parola"))
                .andExpect(jsonPath("$.nrCrediteNecesare").value(50))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(100));

        StudentPutRequest put = new StudentPutRequest("Alex","Stefan","alex@gmail.com",21,"parola",30,50);
        mockMvc.perform(put("/api/v1/books-students/students/edit/update/{studentId}",created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(put)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Stefan"))
                .andExpect(jsonPath("$.email").value("alex@gmail.com"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.password").value("parola"))
                .andExpect(jsonPath("$.nrCrediteNecesare").value(30))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(50));

        StudentPatchRequest patch = new StudentPatchRequest("andrei@gmail.com","parolaparola",90);
        mockMvc.perform(patch("/api/v1/books-students/students/edit/patch/{studentId}",created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.email").value("andrei@gmail.com"))
                .andExpect(jsonPath("$.password").value("parolaparola"))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value("90"));

        mockMvc.perform(delete("/api/v1/books-students/students/{studentId}", created.id()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/books-students/students/get/{studentId}",created.id()))
                .andExpect(status().isNotFound());

    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest(
                "Vlad",
                "Breazu",
                "vlad@gmail.com",
                22,
                "parola",
                50,
                10
        );

        mockMvc.perform(post("/api/v1/books-students/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();


        mockMvc.perform(post("/api/v1/books-students/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void validationErrorAreReturned() throws Exception{
        Student student = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").build();
        BookCreateRequest invalid = new BookCreateRequest(null);

        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

}
