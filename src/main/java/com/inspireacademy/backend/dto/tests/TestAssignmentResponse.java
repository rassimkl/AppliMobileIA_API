package com.inspireacademy.backend.dto.tests;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestAssignmentResponse {
    private Long testId;
    private String title;
    private String langue;
    private Integer durationSeconds;
    private boolean completed;

    private Integer score;
    private Integer maxScore;
}