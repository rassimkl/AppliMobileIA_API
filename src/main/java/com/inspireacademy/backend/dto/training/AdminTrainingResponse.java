package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.DocumentCategory;
import com.inspireacademy.backend.model.training.TrainingType;
import com.inspireacademy.backend.model.training.TrainingVisibility;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTrainingResponse {

    private Long id;
    private String title;
    private String description;

    private Long langueId;
    private String langue;

    private TrainingType type;
    private TrainingVisibility visibility;

    private boolean published;

    private Integer durationSeconds;

    private String fileUrl;
    private String coverUrl;

    private String level;

    private DocumentCategory documentCategory;
    private String customCategory;

    private Long createdById;
    private String createdByEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}