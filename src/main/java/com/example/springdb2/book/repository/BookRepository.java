package com.example.springdb2.book.repository;

import com.example.springdb2.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select (count(b)>0) from Book b where lower(b.name) = :name")
    boolean existsByNameIgnoreCase(@Param ("name") String name);
}
