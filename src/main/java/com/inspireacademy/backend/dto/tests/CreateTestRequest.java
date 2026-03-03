package com.inspireacademy.backend.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTestRequest {

    private String title;
    private String description;
    private Long langueId;
    private Integer durationSeconds;
}