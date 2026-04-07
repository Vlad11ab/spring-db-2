package com.example.springdb2.student.mappers;

import com.example.springdb2.book.mappers.BookMapper;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.model.Student;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    private static PasswordEncoder encode;

    //todo:refactor
    public StudentMapper(PasswordEncoder encode) {
        this.encode=encode;
    }

    public static Student toEntity(StudentCreateRequest req){
        if(req == null) return null;

        return Student.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .age(req.age())
                .password(encode.encode(req.password()))
                .nrCrediteNecesare(req.nrCrediteNecesare())
                .nrCrediteEfectuate(req.nrCrediteEfectuate())
                .build();
    }

    public static StudentResponse toDto(Student student){
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPassword(),
                student.getAge(),
                student.getNrCrediteNecesare(),
                student.getNrCrediteEfectuate(),
                student.getBooks().stream().map(BookMapper::toDto).toList()
        );
    }

    public static StudentCreateResponse studentToDto(Student student,String token){
        return new StudentCreateResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPassword(),
                student.getAge(),
                student.getNrCrediteNecesare(),
                student.getNrCrediteEfectuate(),
                token
        );
    }

}
