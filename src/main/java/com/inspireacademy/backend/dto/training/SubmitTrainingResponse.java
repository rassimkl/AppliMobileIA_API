package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitTrainingResponse {

    private Long attemptId;
    private Integer score;
    private Integer maxScore;
    private LocalDateTime completedAt;
}