package com.example.springdb2.student.repository;

import com.example.springdb2.student.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Override
    @EntityGraph(attributePaths = "books")
    List<Student> findAll();

    @Override
    @EntityGraph(attributePaths = "books")
    Optional<Student> findById(Long studentId);

    Optional<Student> findByEmail(String email);

    @Query("select (count(s)>0) from Student s where lower(s.email) = lower(:email)")
    boolean existsByEmailJpql(@Param("email") String email);
}
