package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.DocumentCategory;
import com.inspireacademy.backend.model.training.TrainingType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDetailsResponse {

    private Long id;
    private String title;
    private String description;

    private String langue;
    private TrainingType type;

    private Integer durationSeconds;

    private String fileUrl;
    private String coverUrl;

    private String level;

    private DocumentCategory documentCategory;
    private String customCategory;

    private List<TrainingQuestionResponse> questions;
}