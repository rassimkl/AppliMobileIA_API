package com.inspireacademy.backend.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponse(
            Long id,
            String email,
            String firstName,
            String lastName,
            String role,
            boolean enabled,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    // getters uniquement (pas de setters)

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
