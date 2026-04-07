package com.example.springdb2.student.model;

import com.example.springdb2.book.model.Book;
import com.example.springdb2.config.security.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "student",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    List<Book> books = new ArrayList<>();

    @Column(name = "first_name")
    @NotBlank
    @Size(min = 3, max = 50, message = "size 3-50")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank
    @Size(min = 3, max = 50, message = "size 3-50")
    private String lastName;

    @Email
    @NotBlank
    @Size(min = 3, max = 50, message = "size 3-50")
    private String email;

    @Column(name = "age")
    @Positive(message = "Age >17")
    private Integer age;

    @NotBlank
    @Size(min = 3, max = 50, message = "size 3-50")
    private String password;

    @Column(name = "nr_credite_necesare_")
    @Positive(message = "Minim Credite Necesare = 35")
    @NotNull
    private Integer nrCrediteNecesare;

    @Column(name = "nr_credite_efectuate_")
    @PositiveOrZero
    private Integer nrCrediteEfectuate;

    public void addBook(Book book){
        this.books.add(book);
        book.setStudent(this);
    }

    public void deleteBook(Book book){
        this.books.remove(book);
        book.setStudent(null);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserRole.STUDENT.getGrantedAuthorities();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
