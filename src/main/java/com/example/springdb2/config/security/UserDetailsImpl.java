package com.example.springdb2.config.security;

import com.example.springdb2.student.repository.StudentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsImpl implements UserDetailsService {
    private final StudentRepository studentRepository;

    public UserDetailsImpl(StudentRepository studentRepo) {
        this.studentRepository = studentRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return (UserDetails) studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }
}