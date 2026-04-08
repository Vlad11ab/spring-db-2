package com.example.springdb2.student.model;

import com.example.springdb2.book.model.Book;
import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "student",
        uniqueConstraints = @UniqueConstraint(name = "uk_student_email", columnNames = "email")
)
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

    @Column(unique = true)
    @Email
    @NotBlank
    @Size(min = 3, max = 50, message = "size 3-50")
    private String email;

    @Column(name = "age")
    @Positive(message = "Age >17")
    private Integer age;

    @NotBlank
    @Size(min = 3, max = 255, message = "size 3-255")
    private String password;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_permissions", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserPermission> permissions = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_permission_groups", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "permission_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<PermissionGroup> permissionGroups = new HashSet<>();

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
        return getEffectivePermissions().stream()
                .map(UserPermission::getPermission)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public Set<UserPermission> getEffectivePermissions() {
        EnumSet<UserPermission> effectivePermissions = EnumSet.noneOf(UserPermission.class);
        if (permissionGroups != null) {
            permissionGroups.stream()
                    .map(PermissionGroup::getPermissions)
                    .forEach(effectivePermissions::addAll);
        }
        if (permissions != null) {
            effectivePermissions.addAll(permissions);
        }
        return effectivePermissions;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
