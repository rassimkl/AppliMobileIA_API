package com.inspireacademy.backend.dto.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminAnswerDetailsResponse {

    private Long questionId;
    private String questionContent;
    private String questionType;
    private Integer questionPoints;

    private String studentAnswer;
    private String correctAnswer;

    private Integer awardedPoints;
    private List<AdminOptionResponse> options;

}