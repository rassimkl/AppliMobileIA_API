package com.inspireacademy.backend.dto.tests;

import com.inspireacademy.backend.model.tests.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateQuestionRequest {

    private String content;
    private QuestionType type;
    private Integer points;
    private String expectedAnswer;
    private Boolean caseSensitive;
    private List<OptionRequest> options;
}