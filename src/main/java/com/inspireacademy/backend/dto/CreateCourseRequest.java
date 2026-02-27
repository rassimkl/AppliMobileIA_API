package com.inspireacademy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCourseRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Long langueId; // ✅ FK vers table langue

    private String level;

    // ================= GETTERS =================

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getLangueId() {   // ✅ IMPORTANT
        return langueId;
    }

    public String getLevel() {
        return level;
    }

    // ================= SETTERS =================

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLangueId(Long langueId) {   // ✅ IMPORTANT
        this.langueId = langueId;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}