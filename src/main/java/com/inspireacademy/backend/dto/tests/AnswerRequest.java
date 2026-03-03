package com.inspireacademy.backend.dto.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {

    private Long questionId;
    private Long selectedOptionId; // null si TEXT
    private String textAnswer;     // null si QCM
}