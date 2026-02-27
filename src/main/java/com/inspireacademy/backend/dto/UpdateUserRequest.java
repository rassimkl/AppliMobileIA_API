package com.inspireacademy.backend.dto;

import java.util.Set;

public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;

    private Set<Long> languages; // IDs des langues

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Set<Long> getLanguages() { return languages; }
}