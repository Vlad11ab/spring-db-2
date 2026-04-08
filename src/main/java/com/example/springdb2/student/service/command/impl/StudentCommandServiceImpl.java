package com.example.springdb2.student.service.command.impl;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.student.dtos.*;
import com.example.springdb2.student.exceptions.EmptyStudentUpdateRequestException;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.mappers.StudentMapper;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.command.StudentCommandService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Component
public class StudentCommandServiceImpl implements StudentCommandService {

    private final StudentRepository studentRepository;
    private final JWTTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final StudentMapper studentMapper;

    public StudentCommandServiceImpl(StudentRepository studentRepository,
                                     JWTTokenProvider jwtTokenProvider,
                                     PasswordEncoder passwordEncoder,
                                     StudentMapper studentMapper){
        this.studentRepository = studentRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional
    public StudentResponse createStudentWithBooks(StudentCreateRequest req) {
        if(studentRepository.existsByEmailJpql(req.email())){
            throw new StudentAlreadyExistsException();
        }
        Student savedStudent = studentRepository.save(studentMapper.toEntity(req));

        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentCreateResponse createStudent(StudentCreateRequest req) {
        if(studentRepository.existsByEmailJpql(req.email())){
            throw new StudentAlreadyExistsException();
        }
        Student savedStudent = studentRepository.save(studentMapper.toEntity(req));


        return studentMapper.studentToCreateResponse(savedStudent, jwtTokenProvider.generateToken(savedStudent));
    }

    @Override
    @Transactional
    public StudentResponse patchStudent(Long studentId, StudentPatchRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if(req.email() != null && !req.email().isBlank()){
            student.setEmail(req.email());
        }
        if(req.password()!=null && !req.password().isBlank()){
            student.setPassword(passwordEncoder.encode(req.password()));
        }
        if(req.nrCrediteEfectuate()!=null){
            student.setNrCrediteEfectuate(req.nrCrediteEfectuate());
        }

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long studentId, StudentPutRequest req) {
        if(
                (req.firstName() == null) &&
                        (req.lastName() == null) &&
                        (req.email() == null) &&
                        (req.age() == null) &&
                        (req.password() == null) &&
                        (req.nrCrediteNecesare() == null) &&
                        (req.nrCrediteEfectuate() == null)
        ){
            throw new EmptyStudentUpdateRequestException();
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if (req.email() != null && !req.email().equals(student.getEmail())) {
            if (studentRepository.existsByEmailJpql(req.email())) {
                throw new StudentAlreadyExistsException();
            }
        }

        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setEmail(req.email());
        student.setAge(req.age());
        student.setPassword(passwordEncoder.encode(req.password()));
        student.setNrCrediteEfectuate(req.nrCrediteEfectuate());
        student.setNrCrediteNecesare(req.nrCrediteNecesare());

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }


    @Override
    @Transactional
    public StudentResponse deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Student deleted = student;
        studentRepository.delete(student);
        return studentMapper.toDto(deleted);
    }

    @Override
    @Transactional
    public StudentResponse updatePermissions(Long studentId, StudentPermissionsUpdateRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        student.setPermissionGroups(EnumSet.copyOf(req.permissionGroups()));
        if (req.directPermissions() == null || req.directPermissions().isEmpty()) {
            student.setPermissions(EnumSet.noneOf(com.example.springdb2.config.security.UserPermission.class));
        } else {
            student.setPermissions(EnumSet.copyOf(req.directPermissions()));
        }

        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }
}
