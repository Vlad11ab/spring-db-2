package com.example.springdb2.config.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.springdb2.config.security.UserPermission.*;

@AllArgsConstructor
public enum UserRole {
        STUDENT(Set.of(BOOK_READ, BOOK_WRITE)),
        ADMIN(Set.of(BOOK_READ,BOOK_WRITE,BOOK_EDIT));

        private final Set<UserPermission> permission;
        public Set<UserPermission> getPermission(){
            return permission;
        }

        public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
            Set<SimpleGrantedAuthority> collect = getPermission()
                    .stream()
                    .map(e->new SimpleGrantedAuthority(e.getPermission()))
                    .collect(Collectors.toSet());

            collect.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
            return collect;
        }


}
