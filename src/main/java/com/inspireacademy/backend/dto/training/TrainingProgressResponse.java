package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.TrainingType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgressResponse {

    private Long trainingId;
    private String title;
    private String langue;
    private TrainingType type;

    private Integer progressPercent;
    private boolean completed;

    private LocalDateTime lastOpenedAt;
}