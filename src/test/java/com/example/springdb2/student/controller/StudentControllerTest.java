package com.example.springdb2.student.controller;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPermissionsUpdateRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class StudentControllerTest extends BaseStudentMvcTestSupport {

    @MockitoBean
    private StudentQueryService studentQueryService;

    @MockitoBean
    private StudentCommandService studentCommandService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void getAllReturnsList() throws Exception {
        when(studentQueryService.getAllStudents()).thenReturn(List.of(
                new StudentResponse(1L, "Andrei", "Popescu", "andrei.popescu@example.com", 20, 60, 30, EnumSet.of(PermissionGroup.STUDENT_BASIC), Collections.emptySet(), EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ, UserPermission.BOOK_WRITE), Collections.emptyList()),
                new StudentResponse(2L, "Maria", "Ionescu", "maria.ionescu@facultate.ro", 22, 180, 180, Collections.emptySet(), EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ), EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ), Collections.emptyList())
        ));

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(jsonPath("$[1].email").value("maria.ionescu@facultate.ro"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void patchReturnsUpdatedStudent() throws Exception {
        StudentPatchRequest request = new StudentPatchRequest("email@gmail.com", "parola", 100);
        StudentResponse response = new StudentResponse(1L, "Vlad", "Breazu", "email@gmail.com", 22, 100, 50, Collections.emptySet(), EnumSet.of(UserPermission.STUDENT_READ), EnumSet.of(UserPermission.STUDENT_READ), Collections.emptyList());

        when(studentCommandService.patchStudent(1L, request)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(studentCommandService).patchStudent(1L, request);
    }

    @Test
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
        doThrow(new StudentNotFoundException(1L)).when(studentCommandService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/students/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT FOUND"));
    }

    @Test
    void updateReturnsUpdatedResponse() throws Exception {
        StudentPutRequest request = new StudentPutRequest("Vlad", "Breazu", "email@gmail.com", 22, "parola", 100, 50);
        StudentResponse response = new StudentResponse(1L, "Vlad", "Breazu", "email@gmail.com", 22, 100, 50, Collections.emptySet(), EnumSet.of(UserPermission.STUDENT_READ), EnumSet.of(UserPermission.STUDENT_READ), Collections.emptyList());

        when(studentCommandService.updateStudent(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Vlad"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(studentCommandService).updateStudent(1L, request);
    }

    @Test
    void updatePermissionsReturnsUpdatedResponse() throws Exception {
        StudentPermissionsUpdateRequest request = new StudentPermissionsUpdateRequest(
                EnumSet.of(PermissionGroup.STUDENT_BASIC, PermissionGroup.BOOK_EDITOR),
                EnumSet.of(UserPermission.STUDENT_PERMISSIONS_EDIT)
        );
        StudentResponse response = new StudentResponse(
                1L, "Vlad", "Breazu", "email@gmail.com", 22, 100, 50,
                EnumSet.of(PermissionGroup.STUDENT_BASIC, PermissionGroup.BOOK_EDITOR),
                EnumSet.of(UserPermission.STUDENT_PERMISSIONS_EDIT),
                EnumSet.of(
                        UserPermission.STUDENT_READ,
                        UserPermission.BOOK_READ,
                        UserPermission.BOOK_WRITE,
                        UserPermission.BOOK_EDIT,
                        UserPermission.STUDENT_PERMISSIONS_EDIT
                ),
                Collections.emptyList()
        );

        when(studentCommandService.updatePermissions(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/students/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.permissionGroups").isArray())
                .andExpect(jsonPath("$.directPermissions").isArray())
                .andExpect(jsonPath("$.effectivePermissions").isArray());

        verify(studentCommandService).updatePermissions(1L, request);
    }
}
