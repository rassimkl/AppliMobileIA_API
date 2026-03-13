package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.DocumentCategory;
import com.inspireacademy.backend.model.training.TrainingType;
import com.inspireacademy.backend.model.training.TrainingVisibility;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTrainingRequest {

    private String title;
    private String description;

    private Long langueId;

    /**
     * EXERCISE ou DOCUMENT
     */
    private TrainingType type;

    /**
     * ASSIGNED_ONLY ou LANGUAGE_PUBLIC
     */
    private TrainingVisibility visibility;

    /**
     * utile uniquement pour EXERCISE
     */
    private Integer durationSeconds;

    /**
     * utile uniquement pour DOCUMENT
     * URL / chemin du PDF
     */
    private String fileUrl;

    private String coverUrl;

    private String level;

    /**
     * utile uniquement pour DOCUMENT
     */
    private DocumentCategory documentCategory;

    /**
     * utile si documentCategory = OTHER
     */
    private String customCategory;
}