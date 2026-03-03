package com.inspireacademy.backend.dto.tests;

import com.inspireacademy.backend.model.tests.QuestionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuestionResponse {

    private Long id;
    private String content;
    private QuestionType type;
    private Integer points;
    private List<OptionResponse> options;
}