package com.inspireacademy.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class CreateUserRequest {

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String firstName;
    private String lastName;

    @NotBlank
    private String role;
    private Set<Long> languages;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public Set<Long> getLanguages() { return languages; }
}