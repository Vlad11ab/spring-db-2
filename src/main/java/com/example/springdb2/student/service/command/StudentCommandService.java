package com.example.springdb2.student.service.command;

import com.example.springdb2.student.dtos.*;

public interface StudentCommandService {

    StudentResponse createStudentWithBooks(StudentCreateRequest req);
    StudentCreateResponse createStudent(StudentCreateRequest req);
    StudentResponse patchStudent(Long studentId, StudentPatchRequest req);
    StudentResponse updateStudent(Long studentId, StudentPutRequest req);
    StudentResponse updatePermissions(Long studentId, StudentPermissionsUpdateRequest req);
    StudentResponse deleteStudent(Long studentId);
}
