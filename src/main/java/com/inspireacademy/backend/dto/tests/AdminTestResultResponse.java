package com.inspireacademy.backend.dto.tests;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTestResultResponse {
    private Long resultId;
    private Long studentId;
    private String studentEmail;
    private Integer score;
    private Integer maxScore;
    private LocalDateTime completedAt;
}