package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingResultResponse {

    private Long attemptId;

    private Long studentId;
    private String studentEmail;
    private String studentFirstName;
    private String studentLastName;

    private Integer score;
    private Integer maxScore;

    private LocalDateTime completedAt;
}