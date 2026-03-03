package com.inspireacademy.backend.dto.tests;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitTestResponse {
    private Long resultId;
    private Integer score;
    private Integer maxScore;
    private LocalDateTime completedAt;
}