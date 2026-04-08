package com.example.springdb2.config.bootstrap;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;

@Component
@Slf4j
public class BootstrapAdminInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Integer age;
    private final Integer nrCrediteNecesare;
    private final Integer nrCrediteEfectuate;

    public BootstrapAdminInitializer(StudentRepository studentRepository,
                                     PasswordEncoder passwordEncoder,
                                     @Value("${application.bootstrap-admin.enabled:true}") boolean enabled,
                                     @Value("${application.bootstrap-admin.firstName}") String firstName,
                                     @Value("${application.bootstrap-admin.lastName}") String lastName,
                                     @Value("${application.bootstrap-admin.email}") String email,
                                     @Value("${application.bootstrap-admin.password}") String password,
                                     @Value("${application.bootstrap-admin.age}") Integer age,
                                     @Value("${application.bootstrap-admin.nrCrediteNecesare}") Integer nrCrediteNecesare,
                                     @Value("${application.bootstrap-admin.nrCrediteEfectuate}") Integer nrCrediteEfectuate) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.nrCrediteNecesare = nrCrediteNecesare;
        this.nrCrediteEfectuate = nrCrediteEfectuate;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (studentRepository.existsByEmailJpql(email)) {
            log.info("Bootstrap super admin already exists: {}", email);
            return;
        }

        Student superAdmin = Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .age(age)
                .password(passwordEncoder.encode(password))
                .permissionGroups(EnumSet.allOf(PermissionGroup.class))
                .permissions(Collections.emptySet())
                .nrCrediteNecesare(nrCrediteNecesare)
                .nrCrediteEfectuate(nrCrediteEfectuate)
                .build();

        studentRepository.save(superAdmin);
        log.info("Bootstrap super admin created: {}", email);
    }
}
