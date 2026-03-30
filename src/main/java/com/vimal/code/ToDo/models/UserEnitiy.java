package com.vimal.code.ToDo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "userDb")
@Inheritance(strategy = InheritanceType.JOINED) // Use JOINED inheritance strategy
public class UserEnitiy implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        private String name;

        @Column(unique = true, nullable = false)
        private String email;

        private String password;



        @Enumerated(EnumType.STRING) // Store the enum as a string in the database
        private Role role;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                // Convert the role to a GrantedAuthority
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        }

        @Override
        public String getUsername() {
                return this.email;
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return true;
        }
}