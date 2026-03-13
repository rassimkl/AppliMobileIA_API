package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.TrainingType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAssignmentResponse {

    private Long trainingId;
    private String title;
    private String langue;
    private TrainingType type;

    private boolean completed;
    private boolean mandatory;

    private Integer score;
    private Integer maxScore;

    private Integer progressPercent;

    private Long attemptId;

    private LocalDateTime assignedAt;
    private LocalDateTime dueDate;
}