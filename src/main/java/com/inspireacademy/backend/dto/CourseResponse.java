package com.inspireacademy.backend.dto;

import java.time.LocalDateTime;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;

    private Long langueId;        // ✅ FK
    private String langueName;    // ✅ Nom affichable

    private String level;
    private String status;
    private LocalDateTime createdAt;

    public CourseResponse(
            Long id,
            String title,
            String description,
            Long langueId,
            String langueName,
            String level,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.langueId = langueId;
        this.langueName = langueName;
        this.level = level;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public Long getLangueId() { return langueId; }
    public String getLangueName() { return langueName; }

    public String getLevel() { return level; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}