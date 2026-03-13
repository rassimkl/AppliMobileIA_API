package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTrainingQuestionResponse {

    private Long id;
    private String content;
    private String type;
    private Integer points;

    /**
     * utile pour TEXT
     */
    private String expectedAnswer;
    private Boolean caseSensitive;

    /**
     * utile pour QCM / ORDER_WORDS / ORDER_SENTENCE
     */
    private List<AdminTrainingOptionResponse> options;
}