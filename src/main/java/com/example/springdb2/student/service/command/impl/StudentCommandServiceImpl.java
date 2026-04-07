package com.example.springdb2.student.service.command.impl;

import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.student.dtos.*;
import com.example.springdb2.student.exceptions.EmptyStudentUpdateRequestException;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.mappers.StudentMapper;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import com.example.springdb2.student.service.command.StudentCommandService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudentCommandServiceImpl implements StudentCommandService {

    private StudentRepository studentRepository;
    private final JWTTokenProvider jwtTokenProvider;

    public StudentCommandServiceImpl(StudentRepository studentRepository, JWTTokenProvider jwtTokenProvider){
        this.studentRepository = studentRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public StudentResponse createStudentWithBooks(StudentCreateRequest req) {
        if(studentRepository.existsByEmailJQPL(req.email())){
            throw new StudentAlreadyExistsException();
        }
        Student savedStudent = studentRepository.save(StudentMapper.toEntity(req));

        return StudentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentCreateResponse createStudent(StudentCreateRequest req) {
        if(studentRepository.existsByEmailJQPL(req.email())){
            throw new StudentAlreadyExistsException();
        }
        Student savedStudent = studentRepository.save(StudentMapper.toEntity(req));


        return StudentMapper.studentToDto(savedStudent,jwtTokenProvider.generateToken(savedStudent));
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
            student.setPassword(req.password());
        }
        if(req.nrCrediteEfectuate()!=null){
            student.setNrCrediteEfectuate(req.nrCrediteEfectuate());
        }

        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
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
            if (studentRepository.existsByEmailJQPL(req.email())) {
                throw new StudentAlreadyExistsException();
            }
        }

        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setEmail(req.email());
        student.setAge(req.age());
        student.setPassword(req.password());
        student.setNrCrediteEfectuate(req.nrCrediteEfectuate());
        student.setNrCrediteNecesare(req.nrCrediteNecesare());

        Student savedStudent = studentRepository.save(student);
        return StudentMapper.toDto(savedStudent);
    }


    @Override
    @Transactional
    public StudentResponse deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Student deleted = student;
        studentRepository.delete(student);
        return StudentMapper.toDto(deleted);
    }
}
