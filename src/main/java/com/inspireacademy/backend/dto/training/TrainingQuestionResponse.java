package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.TrainingQuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingQuestionResponse {

    private Long id;
    private String content;
    private TrainingQuestionType type;
    private Integer points;

    /**
     * utile pour QCM / ORDER_WORDS / ORDER_SENTENCE
     */
    private List<TrainingOptionResponse> options;
}