package com.inspireacademy.backend.dto.tests;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TestDetailsResponse {

    private Long id;
    private String title;
    private String description;
    private Integer durationSeconds;
    private List<QuestionResponse> questions;
}