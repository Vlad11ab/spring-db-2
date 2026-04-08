package com.example.springdb2.student.controller;

import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPermissionsUpdateRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@Slf4j
@Tag(name = "Students", description = "Student management endpoints protected by fine-grained permissions")
public class StudentController {

    private final StudentQueryService studentQueryService;
    private final StudentCommandService studentCommandService;

    public StudentController(StudentQueryService studentQueryService, StudentCommandService studentCommandService){
        this.studentQueryService = studentQueryService;
        this.studentCommandService = studentCommandService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student:read')")
    @Operation(
            summary = "List all students",
            description = "Requires permission student:read. Returns all students with their permission groups, direct permissions and effective permissions.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Students returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:read")
    })
    public ResponseEntity<List<StudentResponse>> getAll(){
        log.info("HTTP GET /api/v1/students");
        return ResponseEntity.status(HttpStatus.OK).body(studentQueryService.getAllStudents());
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:read')")
    @Operation(
            summary = "Get a student by id",
            description = "Requires permission student:read.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student returned successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:read"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> getById(@PathVariable Long studentId){
        log.info("HTTP GET /api/v1/students/{}", studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentQueryService.getStudentById(studentId));
    }

    @PatchMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:edit')")
    @Operation(
            summary = "Patch student profile fields",
            description = "Requires permission student:edit. Updates only the provided student profile fields.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Student patched successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:edit"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> patch(@PathVariable Long studentId, @Valid @RequestBody StudentPatchRequest patched){
        log.info("HTTP PATCH /api/v1/students/{}", studentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studentCommandService.patchStudent(studentId,patched));
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:edit')")
    @Operation(
            summary = "Delete a student",
            description = "Requires permission student:edit.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student deleted successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:edit"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> delete(@PathVariable Long studentId){
        log.info("HTTP DELETE /api/v1/students/{}", studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentCommandService.deleteStudent(studentId));
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAuthority('student:edit')")
    @Operation(
            summary = "Replace student profile",
            description = "Requires permission student:edit. Replaces the editable student profile fields.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Student updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:edit"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<StudentResponse> update(@PathVariable Long studentId, @Valid @RequestBody StudentPutRequest updated){
        log.info("HTTP PUT /api/v1/students/{}", studentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studentCommandService.updateStudent(studentId, updated));
    }

    @PutMapping("/{studentId}/permissions")
    @PreAuthorize("hasAuthority('student:permissions:edit')")
    @Operation(
            summary = "Update student permission groups and direct permissions",
            description = "Requires permission student:permissions:edit. This endpoint manages access rights, not profile fields.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Permissions updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission student:permissions:edit"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> updatePermissions(@PathVariable Long studentId,
                                                             @Valid @RequestBody StudentPermissionsUpdateRequest request) {
        log.info("HTTP PUT /api/v1/students/{}/permissions", studentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studentCommandService.updatePermissions(studentId, request));
    }
}
