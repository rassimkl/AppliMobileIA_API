package com.inspireacademy.backend.dto;

import java.time.LocalDateTime;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String language;
    private String level;
    private String status;
    private LocalDateTime createdAt;

    // 🔥 Constructeur nécessaire
    public CourseResponse(Long id, String title, String description, String language, String level, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.language = language;
        this.level = level;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters uniquement (DTO réponse = lecture seule)

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getLevel() {
        return level;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
