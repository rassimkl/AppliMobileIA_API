package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAnswerDetailsResponse {

    private Long questionId;
    private String questionContent;
    private String questionType;
    private Integer questionPoints;

    private String studentAnswer;
    private String correctAnswer;

    private Integer awardedPoints;

    private List<AdminTrainingOptionResponse> options;
}