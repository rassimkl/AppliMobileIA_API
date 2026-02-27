package com.inspireacademy.backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Set<String> languages;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponse(
            Long id,
            String email,
            String firstName,
            String lastName,
            String role,
            Set<String> languages,
            boolean enabled,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.languages = languages;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public Set<String> getLanguages() { return languages; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}